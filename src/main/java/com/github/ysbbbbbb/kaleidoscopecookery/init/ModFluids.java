package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.MilkFluid;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public final class ModFluids {
    public static final Identifier MILK_ID = Identifier.withDefaultNamespace("milk");

    private ModFluids() {
    }

    public static void registerFluids() {
        Fluid milk = BuiltInRegistries.FLUID.getOptional(MILK_ID).orElseGet(() ->
                Registry.register(BuiltInRegistries.FLUID, MILK_ID, new MilkFluid()));

        FluidVariantAttributes.register(milk, new FluidVariantAttributeHandler() {
            @Override
            public @NonNull Component getName(@NonNull FluidVariant variant) {
                return Component.translatable("fluid.minecraft.milk");
            }

            @Override
            public @NonNull Optional<SoundEvent> getFillSound(@NonNull FluidVariant variant) {
                return Optional.of(SoundEvents.BUCKET_FILL);
            }

            @Override
            public @NonNull Optional<SoundEvent> getEmptySound(@NonNull FluidVariant variant) {
                return Optional.of(SoundEvents.BUCKET_EMPTY);
            }
        });

        FluidStorage.ITEM.registerFallback((stack, context) -> stack.is(Items.MILK_BUCKET)
                ? new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(milk), FluidConstants.BUCKET)
                : null);
        FluidStorage.combinedItemApiProvider(Items.BUCKET).register(context ->
                new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(Items.MILK_BUCKET), milk, FluidConstants.BUCKET));
    }

    public static boolean matchesTeaFluid(Identifier expected, Identifier actual) {
        if (expected.equals(actual)) {
            return true;
        }
        if (!MILK_ID.equals(expected) || !BuiltInRegistries.FLUID.containsKey(actual)) {
            return false;
        }
        return BuiltInRegistries.FLUID.getValue(actual).getBucket() == Items.MILK_BUCKET;
    }
}
