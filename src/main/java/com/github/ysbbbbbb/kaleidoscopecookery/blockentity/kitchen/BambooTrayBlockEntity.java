package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IBambooTray;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.BambooTrayRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagCommon;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.forge.IItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class BambooTrayBlockEntity extends BaseBlockEntity implements WorldlyContainer, IBambooTray {
    private static final int[] SLOTS = {0, 1, 2, 3};
    private static final String PROGRESS_TAG = "ProcessingProgress";
    private static final String DURATIONS_TAG = "ProcessingDurations";
    private static final String COMPLETION_STATES_TAG = "CompletionStates";

    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private final int[] processingProgress = new int[4];
    private final int[] processingDurations = new int[4];
    private final CompletionState[] completionStates = new CompletionState[4];


    public BambooTrayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.BAMBOO_TRAY_BE, pos, state);
        Arrays.fill(this.completionStates, CompletionState.NONE);
    }

    @SuppressWarnings("unused")
    public static void serverTick(Level level, BlockPos pos, BlockState state, BambooTrayBlockEntity tray) {
        if (level.isClientSide) {
            return;
        }

        long offset = pos.getX() + pos.getY() + pos.getZ();
        if ((level.getGameTime() + offset) % 19 != 0) {
            return;
        }

        boolean wetting = level.isRainingAt(pos.above()) || hasWaterDripstone(level, pos);
        boolean drying = !level.isRaining() && hasDryingExposure(level, pos);
        if (!wetting && !drying) {
            return;
        }
        BambooTrayRecipe.Subtype subtype = wetting
                ? BambooTrayRecipe.Subtype.WETTING
                : BambooTrayRecipe.Subtype.DRYING;

        boolean changed = false;
        List<BambooTrayRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.BAMBOO_TRAY_RECIPE);
        for (int slot = 0; slot < tray.items.size(); slot++) {
            ItemStack input = tray.items.get(slot);
            CompletionState completionState = tray.completionStates[slot];
            if (input.isEmpty() || completionState.matches(subtype)) {
                continue;
            }

            BambooTrayRecipe recipe = recipes.stream()
                    .filter(candidate -> candidate.getSubtype() == subtype)
                    .filter(candidate -> candidate.getIngredient().test(input))
                    .findFirst()
                    .orElse(null);
            if (recipe == null) {
                if (tray.processingProgress[slot] != 0 || tray.processingDurations[slot] != 0) {
                    tray.processingProgress[slot] = 0;
                    tray.processingDurations[slot] = 0;
                    changed = true;
                }
                continue;
            }

            if (completionState.isCompleted()) {
                tray.processingProgress[slot] = 0;
                tray.completionStates[slot] = CompletionState.NONE;
            }
            tray.processingDurations[slot] = recipe.getDuration();
            tray.processingProgress[slot] += 19;
            changed = true;

            if (tray.processingProgress[slot] >= recipe.getDuration()) {
                ItemStack result = recipe.getResult().copy();
                int count = Math.min(result.getMaxStackSize(), result.getCount() * input.getCount());
                result.setCount(count);

                tray.items.set(slot, result);
                tray.processingProgress[slot] = recipe.getDuration();
                tray.completionStates[slot] = CompletionState.fromSubtype(subtype);
            }
        }

        if (changed) {
            tray.refresh();
        }
    }

    public int getProgressPercent(int slot) {
        if (slot < 0 || slot >= this.items.size() || this.items.get(slot).isEmpty()) {
            return 0;
        }
        if (this.completionStates[slot].isCompleted()) {
            return 100;
        }
        int duration = this.processingDurations[slot];
        return duration <= 0 ? 0 : (int) Math.min(100L, Math.max(this.processingProgress[slot] * 100L / duration, 0L));
    }

    private static boolean hasDryingExposure(Level level, BlockPos pos) {
        BlockPos above = pos.above();
        if (level.canSeeSky(above)) {
            return true;
        }
        for (int y = above.getY(); y < level.getMaxBuildHeight(); y++) {
            BlockState state = level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ()));
            if (state.isAir() || state.is(TagCommon.COLORLESS_GLASS) || state.is(TagCommon.COLORLESS_GLASS_PANES) || state.is(ModBlocks.BAMBOO_TRAY)) {
                continue;
            }
            return false;
        }
        return true;
    }

    private static boolean hasWaterDripstone(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        BlockPos tipPos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(level, pos);
        return tipPos != null && PointedDripstoneBlock.getCauldronFillFluidType(serverLevel, tipPos) == Fluids.WATER;
    }

    @Override
    public boolean onPutItem(Level level, LivingEntity user, ItemStack held, int slot) {
        if (slot < 0 || slot >= this.items.size() || held.isEmpty()) {
            return false;
        }

        int moved = insertIntoSlot(slot, held);
        if (moved <= 0) {
            return false;
        }
        if (!(user instanceof Player player && player.getAbilities().instabuild)) {
            held.shrink(moved);
        }
        user.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
        this.refresh();
        return true;
    }

    @Override
    public boolean onTakeOut(Level level, LivingEntity user, int slot, boolean takeAll) {
        if (slot < 0 || slot >= this.items.size()) {
            return false;
        }

        ItemStack stored = this.items.get(slot);
        if (stored.isEmpty()) {
            return false;
        }

        int amount = takeAll ? stored.getCount() : 1;
        ItemStack extracted = stored.split(amount);
        if (stored.isEmpty()) {
            this.items.set(slot, ItemStack.EMPTY);
            resetProcessing(slot);
        }
        ItemUtils.getItemToLivingEntity(user, extracted);
        user.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
        this.refresh();
        return true;
    }

    private int insertIntoSlot(int slot, ItemStack stack) {
        ItemStack stored = this.items.get(slot);
        if (stored.isEmpty()) {
            int moved = Math.min(stack.getCount(), stack.getMaxStackSize());
            ItemStack inserted = stack.copy();
            inserted.setCount(moved);
            this.items.set(slot, inserted);
            resetProcessing(slot);
            return moved;
        }
        if (this.completionStates[slot].isCompleted() || !ItemStack.isSameItemSameTags(stored, stack)) {
            return 0;
        }
        int moved = Math.min(stack.getCount(), stored.getMaxStackSize() - stored.getCount());
        stored.grow(moved);
        return moved;
    }

    private void resetProcessing(int slot) {
        this.processingProgress[slot] = 0;
        this.processingDurations[slot] = 0;
        this.completionStates[slot] = CompletionState.NONE;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items, true);
        tag.putIntArray(PROGRESS_TAG, this.processingProgress);
        tag.putIntArray(DURATIONS_TAG, this.processingDurations);

        byte[] completionValues = new byte[this.completionStates.length];
        for (int i = 0; i < this.completionStates.length; i++) {
            completionValues[i] = this.completionStates[i].getSerializedValue();
        }
        tag.putByteArray(COMPLETION_STATES_TAG, completionValues);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.items.clear();

        Arrays.fill(this.processingProgress, 0);
        Arrays.fill(this.processingDurations, 0);
        Arrays.fill(this.completionStates, CompletionState.NONE);
        ContainerHelper.loadAllItems(tag, this.items);

        int[] progress = tag.getIntArray(PROGRESS_TAG);
        System.arraycopy(progress, 0, this.processingProgress, 0, Math.min(progress.length, this.processingProgress.length));
        int[] durations = tag.getIntArray(DURATIONS_TAG);
        System.arraycopy(durations, 0, this.processingDurations, 0, Math.min(durations.length, this.processingDurations.length));
        byte[] completionValues = tag.getByteArray(COMPLETION_STATES_TAG);
        for (int i = 0; i < Math.min(completionValues.length, this.completionStates.length); i++) {
            this.completionStates[i] = CompletionState.fromSerializedValue(completionValues[i]);
        }
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction side) {
        if (side == Direction.DOWN || this.completionStates[slot].isCompleted()) {
            return false;
        }
        ItemStack stored = this.items.get(slot);
        return stored.isEmpty() || ItemStack.isSameItemSameTags(stored, stack) && stored.getCount() < stored.getMaxStackSize();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction side) {
        return side == Direction.DOWN && this.completionStates[slot].isCompleted();
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
        if (!result.isEmpty()) {
            if (this.items.get(slot).isEmpty()) {
                resetProcessing(slot);
            }
            this.refresh();
        }
        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ContainerHelper.takeItem(this.items, slot);
        if (!result.isEmpty()) {
            resetProcessing(slot);
        }
        return result;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        ItemStack previous = this.items.get(slot);
        this.items.set(slot, stack);
        if (stack.isEmpty() || this.completionStates[slot].isCompleted() || !ItemStack.isSameItemSameTags(previous, stack)) {
            resetProcessing(slot);
        }
        this.refresh();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        Arrays.fill(this.processingProgress, 0);
        Arrays.fill(this.processingDurations, 0);
        Arrays.fill(this.completionStates, CompletionState.NONE);
        this.refresh();
    }

    /** Fabric端竹匾物品处理器 */
    public IItemHandler getItemHandler(Direction side) {
        return new TrayItemHandler(side);
    }

    private final class TrayItemHandler implements IItemHandler {

        private final Direction side;

        private TrayItemHandler(Direction side) {
            this.side = side;
        }

        private int actual(int slot) {
            return getSlotsForFace(side)[slot];
        }

        @Override
        public int getSlots() {
            return getSlotsForFace(side).length;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return getItem(actual(slot));
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            int target = actual(slot);
            if (!canPlaceItemThroughFace(target, stack, side))
                return stack;
            if (simulate) {
                ItemStack stored = getItem(target);
                int capacity = stored.isEmpty() ? stack.getMaxStackSize() : stored.getMaxStackSize() - stored.getCount();
                int moved = Math.min(stack.getCount(), Math.max(0, capacity));
                ItemStack remainder = stack.copy();
                remainder.shrink(moved);
                return remainder;
            }
            int moved = insertIntoSlot(target, stack);
            if (moved <= 0) return stack;
            ItemStack remainder = stack.copy();
            remainder.shrink(moved);
            return remainder;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            int target = actual(slot);
            if (!canTakeItemThroughFace(target, getItem(target), side))
                return ItemStack.EMPTY;
            int count = Math.min(amount, getItem(target).getCount());
            return simulate ? getItem(target).copyWithCount(count) : removeItem(target, count);
        }

        @Override public int getSlotLimit(int slot) {
            return getMaxStackSize();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return canPlaceItemThroughFace(actual(slot), stack, side);
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            setItem(actual(slot), stack);
        }
    }

    private enum CompletionState {
        NONE((byte) 0),
        DRYING_COMPLETED((byte) 1),
        WETTING_COMPLETED((byte) 2);

        private final byte serializedValue;

        CompletionState(byte serializedValue) {
            this.serializedValue = serializedValue;
        }

        private boolean isCompleted() {
            return this != NONE;
        }

        private boolean matches(BambooTrayRecipe.Subtype subtype) {
            return (this == DRYING_COMPLETED && subtype == BambooTrayRecipe.Subtype.DRYING)
                   || (this == WETTING_COMPLETED && subtype == BambooTrayRecipe.Subtype.WETTING);
        }

        private byte getSerializedValue() {
            return this.serializedValue;
        }

        private static CompletionState fromSubtype(BambooTrayRecipe.Subtype subtype) {
            return subtype == BambooTrayRecipe.Subtype.DRYING ? DRYING_COMPLETED : WETTING_COMPLETED;
        }

        private static CompletionState fromSerializedValue(byte value) {
            for (CompletionState state : values()) {
                if (state.serializedValue == value) {
                    return state;
                }
            }
            return NONE;
        }
    }
}
