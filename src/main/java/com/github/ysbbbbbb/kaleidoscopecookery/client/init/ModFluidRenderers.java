package com.github.ysbbbbbb.kaleidoscopecookery.client.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

@Environment(EnvType.CLIENT)
public final class ModFluidRenderers {
    public static void register() {
        Fluid milk = BuiltInRegistries.FLUID.get(ModFluids.MILK_ID);
        if (FluidRenderHandlerRegistry.INSTANCE.get(milk) == null) {
            ResourceLocation texture = new ResourceLocation(KaleidoscopeCookery.MOD_ID, "stockpot/milk");
            FluidRenderHandlerRegistry.INSTANCE.register(milk, new SimpleFluidRenderHandler(texture, texture));
        }
    }
}
