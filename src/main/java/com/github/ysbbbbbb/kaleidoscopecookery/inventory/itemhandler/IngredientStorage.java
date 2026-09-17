package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class IngredientStorage extends SingleStackStorage {
    private final Supplier<ItemStack> getter;
    private final Consumer<ItemStack> setter;
    private final Predicate<ItemStack> acceptsInput;
    private final Runnable onCommit;
    private final int capacity;

    public IngredientStorage(Supplier<ItemStack> getter, Consumer<ItemStack> setter,
                             Predicate<ItemStack> acceptsInput, int capacity, Runnable onCommit) {
        this.getter = getter;
        this.setter = setter;
        this.acceptsInput = acceptsInput;
        this.capacity = capacity;
        this.onCommit = onCommit;
    }

    @Override
    protected ItemStack getStack() {
        return getter.get();
    }

    @Override
    protected void setStack(ItemStack stack) {
        setter.accept(stack);
    }

    @Override
    protected boolean canInsert(ItemVariant variant) {
        return acceptsInput.test(variant.toStack());
    }

    @Override
    protected boolean canExtract(ItemVariant variant) {
        return false;
    }

    @Override
    public boolean supportsExtraction() {
        return false;
    }

    @Override
    protected int getCapacity(ItemVariant variant) {
        return variant.isBlank() ? capacity : Math.min(capacity, variant.toStack().getMaxStackSize());
    }

    @Override
    protected void onFinalCommit() {
        onCommit.run();
    }
}
