package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IBambooTray;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.BambooTrayBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BambooTrayBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty STAND = BooleanProperty.create("stand");
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public BambooTrayBlock() {
        super(Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(0.5F)
                .sound(SoundType.BAMBOO)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY)
                .ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any().setValue(STAND, false).setValue(WATERLOGGED, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND || hitResult.getDirection() != Direction.UP) {
            return InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof IBambooTray tray)) {
            return InteractionResult.PASS;
        }
        // 禁止自己摆放为物品
        if (player.getMainHandItem().is(this.asItem())) {
            return InteractionResult.PASS;
        }

        double localX = hitResult.getLocation().x - pos.getX();
        double localZ = hitResult.getLocation().z - pos.getZ();
        int slot = (localZ >= 0.5 ? 2 : 0) + (localX >= 0.5 ? 1 : 0);

        ItemStack held = player.getMainHandItem();
        boolean handled = held.isEmpty()
                ? tray.onTakeOut(level, player, slot, player.isShiftKeyDown())
                : tray.onPutItem(level, player, held, slot);
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState,
                                           @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            return state.setValue(STAND, neighborState.is(this));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAND, WATERLOGGED);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos below = context.getClickedPos().below();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        boolean sameBlock = context.getLevel().getBlockState(below).is(this);
        return super.defaultBlockState().setValue(STAND, sameBlock).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BambooTrayBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            @NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType
    ) {
        return createTickerHelper(blockEntityType, ModBlocks.BAMBOO_TRAY_BE, BambooTrayBlockEntity::serverTick);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> actualType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return actualType == expectedType ? (BlockEntityTicker<A>) ticker : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof BambooTrayBlockEntity tray) {
            for (ItemStack stack : tray.getItems()) {
                if (!stack.isEmpty()) {
                    drops.add(stack.copy());
                }
            }
        }
        return drops;
    }
}
