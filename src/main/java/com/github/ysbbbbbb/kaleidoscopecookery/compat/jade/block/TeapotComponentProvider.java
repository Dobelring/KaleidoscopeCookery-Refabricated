package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@SuppressWarnings("all")
public enum TeapotComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof TeapotBlockEntity teapot)) {
            return;
        }

        if (teapot.getStatus() == ITeapot.FINISHED) {
            ItemStack result = teapot.getResult();
            if (!result.isEmpty()) {
                tooltip.add(Component.translatable("jade.kaleidoscope_cookery.teapot.result", result.getHoverName()));
            }
            return;
        }

        ResourceLocation fluidId = teapot.getTeaFluidId();
        if (!TeapotRecipeSerializer.EMPTY_TEA_FLUID.equals(fluidId)) {
            Fluid fluid = BuiltInRegistries.FLUID.get(fluidId);
            tooltip.add(Component.translatable("jade.kaleidoscope_cookery.teapot.fluid", FluidVariantAttributes.getName(FluidVariant.of(fluid)).copy()));
        }

        ItemStack input = teapot.getInput();
        if (!input.isEmpty()) {
            tooltip.add(Component.translatable("jade.kaleidoscope_cookery.teapot.ingredient", input.getHoverName()));
        }

        if (teapot.getStatus() == ITeapot.PROCESSING) {
            long remainingSeconds = (Math.max(0L, teapot.getCurrentTick()) + 19L) / 20L;
            tooltip.add(Component.translatable("jade.kaleidoscope_cookery.teapot.remaining_time", remainingSeconds));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ModPlugin.TEAPOT;
    }
}
