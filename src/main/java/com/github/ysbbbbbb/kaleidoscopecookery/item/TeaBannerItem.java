package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TeaBannerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class TeaBannerItem extends BlockItem {
    public TeaBannerItem(net.minecraft.world.level.block.Block block, Item.Properties properties) {
        super(block, properties.stacksTo(1));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag flag) {
        TypedEntityData<?> data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        CompoundTag tag = data == null ? null : data.copyTagWithoutId();
        DyeColor color = tag == null ? DyeColor.RED : TeaBannerBlockEntity.getColor(tag);
        HolderLookup.Provider registries = context.registries();
        ItemStack patternItem = tag == null || registries == null || !tag.contains(TeaBannerBlockEntity.PATTERN_ITEM_TAG)
                ? ItemStack.EMPTY
                : ItemStack.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE),
                        tag.getCompoundOrEmpty(TeaBannerBlockEntity.PATTERN_ITEM_TAG)).result().orElse(ItemStack.EMPTY);
        if (!TeaBannerBlockEntity.isSupportedPattern(patternItem)) {
            patternItem = ItemStack.EMPTY;
        }

        MutableComponent pattern = Component.translatable("tooltip.kaleidoscope_cookery.tea_banner.pattern.tea");
        if (!patternItem.isEmpty()) {
            pattern = patternItem.getHoverName().copy();
        }
        MutableComponent colorName = Component.translatable("color.minecraft." + color.getName());

        consumer.accept(Component.translatable("tooltip.kaleidoscope_cookery.tea_banner.color", colorName)
                .withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.translatable("tooltip.kaleidoscope_cookery.tea_banner.pattern", pattern)
                .withStyle(ChatFormatting.GRAY));
    }
}
