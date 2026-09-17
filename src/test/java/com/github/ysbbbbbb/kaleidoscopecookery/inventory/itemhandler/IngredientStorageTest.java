package com.github.ysbbbbbb.kaleidoscopecookery.inventory.itemhandler;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class IngredientStorageTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void abortedInsertionRestoresSlotWithoutStartingProcessing() {
        AtomicReference<ItemStack> input = new AtomicReference<>(ItemStack.EMPTY);
        AtomicInteger commits = new AtomicInteger();
        IngredientStorage storage = storage(input, commits, 1);
        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(1, storage.insert(ItemVariant.of(Items.WHEAT), 64, transaction));
            assertEquals(1, input.get().getCount());
            assertEquals(0, commits.get());
        }
        assertTrue(input.get().isEmpty());
        assertEquals(0, commits.get());
    }

    @Test
    void committedInsertionUsesCapacityAndCannotExtract() {
        AtomicReference<ItemStack> input = new AtomicReference<>(ItemStack.EMPTY);
        AtomicInteger commits = new AtomicInteger();
        IngredientStorage storage = storage(input, commits, 8);
        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(8, storage.insert(ItemVariant.of(Items.WHEAT), 64, transaction));
            assertEquals(0, storage.insert(ItemVariant.of(Items.WHEAT), 1, transaction));
            assertEquals(0, storage.extract(ItemVariant.of(Items.WHEAT), 8, transaction));
            transaction.commit();
        }
        assertEquals(8, input.get().getCount());
        assertEquals(1, commits.get());
        assertFalse(storage.supportsExtraction());
    }

    @Test
    void nestedCommitStillRollsBackWithOuterTransaction() {
        AtomicReference<ItemStack> input = new AtomicReference<>(ItemStack.EMPTY);
        AtomicInteger commits = new AtomicInteger();
        IngredientStorage storage = storage(input, commits, 1);
        try (Transaction outer = Transaction.openOuter()) {
            try (Transaction nested = outer.openNested()) {
                assertEquals(1, storage.insert(ItemVariant.of(Items.WHEAT), 1, nested));
                nested.commit();
            }
            assertEquals(0, commits.get());
        }
        assertTrue(input.get().isEmpty());
        assertEquals(0, commits.get());
    }

    @Test
    void refusesInvalidIngredientsAndZeroAmounts() {
        AtomicReference<ItemStack> input = new AtomicReference<>(ItemStack.EMPTY);
        AtomicInteger commits = new AtomicInteger();
        IngredientStorage storage = storage(input, commits, 1);
        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(0, storage.insert(ItemVariant.of(Items.STONE), 1, transaction));
            assertEquals(0, storage.insert(ItemVariant.of(Items.WHEAT), 0, transaction));
            transaction.commit();
        }
        assertTrue(input.get().isEmpty());
        assertEquals(0, commits.get());
    }

    private static IngredientStorage storage(AtomicReference<ItemStack> input, AtomicInteger commits, int capacity) {
        return new IngredientStorage(input::get, input::set,
                stack -> input.get().isEmpty() && stack.is(Items.WHEAT), capacity, commits::incrementAndGet);
    }
}
