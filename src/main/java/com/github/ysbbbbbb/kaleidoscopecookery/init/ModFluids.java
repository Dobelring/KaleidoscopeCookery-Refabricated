package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.MilkFluid;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public final class ModFluids {
    public static final ResourceLocation MILK_ID = new ResourceLocation("minecraft", "milk");

    public static void registerFluids() {
        Fluid milk = BuiltInRegistries.FLUID.getOptional(MILK_ID).orElseGet(() ->
                Registry.register(BuiltInRegistries.FLUID, MILK_ID, new MilkFluid()));
        FluidVariantAttributes.register(milk, new FluidVariantAttributeHandler() {
            @Override
            public Component getName(FluidVariant variant) {
                return Items.MILK_BUCKET.getDescription();
            }

            @Override
            public Optional<SoundEvent> getFillSound(FluidVariant variant) {
                return Optional.of(SoundEvents.BUCKET_FILL);
            }

            @Override
            public Optional<SoundEvent> getEmptySound(FluidVariant variant) {
                return Optional.of(SoundEvents.BUCKET_EMPTY);
            }
        });

        //倾倒逻辑兼容性注册
        FluidStorage.ITEM.registerFallback((stack, context) -> stack.is(Items.MILK_BUCKET)
                ? new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(milk), FluidConstants.BUCKET)
                : null);
        //装桶逻辑兼容性注册
        FluidStorage.combinedItemApiProvider(Items.BUCKET).register(context ->
                new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(Items.MILK_BUCKET), milk, FluidConstants.BUCKET));
        KaleidoscopeCookery.LOGGER.debug("Registered milk fluid and fallback bucket storage: {}", MILK_ID);
    }

    // 兼容其他模组的牛奶注册类型
    public static boolean matchesTeaFluid(ResourceLocation expected, ResourceLocation actual) {
        return expected.equals(actual) || (MILK_ID.equals(expected)
                && BuiltInRegistries.FLUID.get(actual).getBucket() == Items.MILK_BUCKET);
    }
}
