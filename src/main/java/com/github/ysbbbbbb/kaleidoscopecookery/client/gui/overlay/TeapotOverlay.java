package com.github.ysbbbbbb.kaleidoscopecookery.client.gui.overlay;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;

import net.minecraft.core.registries.BuiltInRegistries;

@Environment(EnvType.CLIENT)
public class TeapotOverlay {
    public static void register() {
        HudRenderCallback.EVENT.register(TeapotOverlay::render);
    }

    private static void render(GuiGraphics graphics, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui) {
            return;
        }
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        LocalPlayer player = minecraft.player;
        HitResult hitResult = minecraft.hitResult;
        if (minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR
                || player == null || !(hitResult instanceof BlockHitResult blockHit)
                || blockHit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        Level level = player.level();
        if (!level.getBlockState(blockHit.getBlockPos()).is(ModBlocks.TEAPOT)
                || !(level.getBlockEntity(blockHit.getBlockPos()) instanceof TeapotBlockEntity teapot)) {
            return;
        }

        int x = screenWidth / 2;
        int y = screenHeight - 72;
        if (minecraft.gui.overlayMessageTime > 0) {
            y += 12;
        }

        Font font = minecraft.font;
        drawCentered(graphics, font, getStatusText(teapot, level), x, y);
        drawCentered(graphics, font, getInfoText(teapot), x, y + 11);
    }

    private static Component getStatusText(TeapotBlockEntity teapot, Level level) {
        if (teapot.getStatus() == ITeapot.FINISHED) {
            return Component.translatable("tooltip.kaleidoscope_cookery.teapot.progress.finished");
        }
        if (!teapot.hasHeatSource(level)) {
            return Component.translatable("tooltip.kaleidoscope_cookery.teapot.progress.no_heat");
        }
        if (teapot.getTeaFluidId().equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
            return Component.translatable("tooltip.kaleidoscope_cookery.teapot.progress.no_fluid");
        }
        if (teapot.getInput().isEmpty()) {
            return Component.translatable("tooltip.kaleidoscope_cookery.teapot.progress.no_tea_base");
        }
        return Component.translatable("tooltip.kaleidoscope_cookery.teapot.progress.processing");
    }

    private static Component getInfoText(TeapotBlockEntity teapot) {
        if (teapot.getStatus() == ITeapot.FINISHED) {
            return Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.result",
                    getItemName(teapot.getResult()));
        }
        return Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.fluid_ingredient",
                getFluidName(teapot.getTeaFluidId()), getItemName(teapot.getInput()));
    }

    private static Component getFluidName(ResourceLocation id) {
        if (id.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
            return Component.translatable("mco.configure.world.slot.empty");
        }
        Fluid fluid = BuiltInRegistries.FLUID.get(id);
        return fluid == null ? Component.literal(id.toString())
                : FluidVariantAttributes.getName(FluidVariant.of(fluid));
    }

    private static Component getItemName(ItemStack stack) {
        return stack.isEmpty() ? Component.translatable("mco.configure.world.slot.empty") : stack.getHoverName();
    }

    private static void drawCentered(GuiGraphics graphics, Font font, Component text, int x, int y) {
        graphics.drawString(font, text, x - font.width(text) / 2, y, 0xFFFFFF);
    }
}
