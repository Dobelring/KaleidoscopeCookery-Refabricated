package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;

public final class TeapotIngredientStorage extends SingleStackStorage {
    private final TeapotBlockEntity teapot;

    public TeapotIngredientStorage(TeapotBlockEntity teapot) {
        this.teapot = teapot;
    }

    @Override
    protected ItemStack getStack() {
        return teapot.getInput();
    }

    @Override
    protected void setStack(ItemStack stack) {
        teapot.setTransferInput(stack);
    }

    @Override
    protected boolean canInsert(ItemVariant variant) {
        return teapot.canInsertIngredient(variant.toStack());
    }

    @Override
    protected boolean canExtract(ItemVariant variant) {
        return false;
    }

    @Override
    protected int getCapacity(ItemVariant variant) {
        return variant.isBlank() ? 1 : teapot.getIngredientCapacity(variant.toStack());
    }

    @Override
    protected void onFinalCommit() {
        teapot.onIngredientTransferCommitted();
    }
}
