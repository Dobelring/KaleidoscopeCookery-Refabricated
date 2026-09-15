package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** One-way ingredient slot. Aborted transfer transactions never start cooking or send updates. */
public final class SingleIngredientStorage extends SingleStackStorage {
    private final Supplier<ItemStack> getter;
    private final Consumer<ItemStack> setter;
    private final BooleanSupplier acceptsInput;
    private final Runnable onCommit;

    public SingleIngredientStorage(Supplier<ItemStack> getter, Consumer<ItemStack> setter,
                                   BooleanSupplier acceptsInput, Runnable onCommit) {
        this.getter = getter;
        this.setter = setter;
        this.acceptsInput = acceptsInput;
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
        return acceptsInput.getAsBoolean();
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
        return 1;
    }

    @Override
    protected void onFinalCommit() {
        onCommit.run();
    }
}
