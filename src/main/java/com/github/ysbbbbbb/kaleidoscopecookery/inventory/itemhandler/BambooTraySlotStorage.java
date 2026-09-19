package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.BambooTrayBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

/** Keeps processing metadata intact during simulated or aborted inventory transfers. */
public final class BambooTraySlotStorage extends SingleStackStorage {
    private final BambooTrayBlockEntity tray;
    private final int slot;

    public BambooTraySlotStorage(BambooTrayBlockEntity tray, int slot) {
        this.tray = tray;
        this.slot = slot;
    }

    @Override
    protected ItemStack getStack() {
        return tray.getItem(slot);
    }

    @Override
    protected void setStack(ItemStack stack) {
        tray.setTransferStack(slot, stack);
    }

    @Override
    protected boolean canInsert(ItemVariant variant) {
        return !tray.isRemoved() && tray.canPlaceItemThroughFace(slot, variant.toStack(), Direction.UP);
    }

    @Override
    protected boolean canExtract(ItemVariant variant) {
        return !tray.isRemoved() && tray.canTakeItemThroughFace(slot, variant.toStack(), Direction.DOWN);
    }

    @Override
    protected int getCapacity(ItemVariant variant) {
        return variant.isBlank() ? 64 : variant.toStack().getMaxStackSize();
    }

    @Override
    protected void onFinalCommit() {
        tray.onTransferCommitted(slot);
    }
}
