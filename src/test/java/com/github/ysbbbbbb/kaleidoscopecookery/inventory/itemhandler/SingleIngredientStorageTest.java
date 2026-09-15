package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingleIngredientStorageTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void simulationDoesNotConsumeItemsOrStartCooking() {
        Slot slot = new Slot();
        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(1, slot.storage.insert(variant(), 64, transaction));
            assertEquals(0, slot.commits);
        }
        assertTrue(slot.stack.isEmpty());
        assertEquals(0, slot.commits);
    }

    @Test
    void nestedCommitStillRollsBackWithOuterTransaction() {
        Slot slot = new Slot();
        try (Transaction outer = Transaction.openOuter()) {
            try (Transaction nested = outer.openNested()) {
                assertEquals(1, slot.storage.insert(variant(), 64, nested));
                nested.commit();
            }
            assertEquals(0, slot.commits);
        }
        assertTrue(slot.stack.isEmpty());
        assertEquals(0, slot.commits);
    }

    @Test
    void committedInsertionPreservesNbtAndAcceptsOnlyOneItem() {
        Slot slot = new Slot();
        ItemStack input = new ItemStack(Items.PAPER, 64);
        input.getOrCreateTag().putString("TeaBlend", "custom");
        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(1, slot.storage.insert(ItemVariant.of(input), 64, transaction));
            assertEquals(0, slot.storage.insert(ItemVariant.of(input), 64, transaction));
            transaction.commit();
        }
        assertEquals(1, slot.stack.getCount());
        assertEquals("custom", slot.stack.getTag().getString("TeaBlend"));
        assertEquals(64, input.getCount());
        assertEquals(1, slot.commits);
    }

    @Test
    void lockedSlotAndExtractionCannotChangeContents() {
        Slot slot = new Slot();
        slot.accepting = false;
        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(0, slot.storage.insert(variant(), 1, transaction));
            transaction.commit();
        }
        slot.stack = variant().toStack();
        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(0, slot.storage.extract(variant(), 1, transaction));
            transaction.commit();
        }
        assertEquals(1, slot.stack.getCount());
        assertEquals(0, slot.commits);
    }

    // NBT variants do not require the ItemVariantCache mixin; plain items are covered by GameTest.
    private static ItemVariant variant() {
        CompoundTag tag = new CompoundTag();
        tag.putString("TeaBlend", "test");
        return ItemVariant.of(Items.PAPER, tag);
    }

    private static class Slot {
        ItemStack stack = ItemStack.EMPTY;
        boolean accepting = true;
        int commits;
        final SingleIngredientStorage storage = new SingleIngredientStorage(
                () -> stack, value -> stack = value, () -> accepting && stack.isEmpty(), () -> commits++);
    }
}
