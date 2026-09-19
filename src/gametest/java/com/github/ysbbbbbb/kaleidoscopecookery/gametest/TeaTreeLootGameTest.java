package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.TeaTreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TeaTreeLootGameTest {
    private static final ResourceKey<LootTable> TEA_TREE_LOOT = ResourceKey.create(Registries.LOOT_TABLE,
            Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "blocks/tea_tree"));
    private static final int SAMPLES = 64;

    @GameTest
    public void immatureTeaTreeOnlyDropsOneSeed(GameTestHelper helper) {
        for (int age = 0; age < TeaTreeBlock.MAX_AGE; age++) {
            var drops = drops(helper, age, ItemStack.EMPTY, null, 1L);
            helper.assertTrue(drops.size() == 1 && count(drops, ModItems.TEA_SEED) == 1,
                    Component.literal("Immature tea tree must drop exactly one seed at age " + age));
        }
        helper.succeed();
    }

    @GameTest
    public void matureTeaTreeDropsLeavesAndFortuneSeeds(GameTestHelper helper) {
        var tool = new ItemStack(Items.DIAMOND_HOE);
        tool.enchant(helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FORTUNE), 3);
        int normalSeeds = 0;
        int fortuneSeeds = 0;
        for (long seed = 1; seed <= SAMPLES; seed++) {
            var normal = drops(helper, TeaTreeBlock.MAX_AGE, ItemStack.EMPTY, null, seed);
            var fortune = drops(helper, TeaTreeBlock.MAX_AGE, tool, null, seed);
            helper.assertTrue(count(normal, ModItems.FRESH_TEA_LEAVES) == 1
                            && count(fortune, ModItems.FRESH_TEA_LEAVES) == 1,
                    Component.literal("Mature tea tree must drop one fresh tea leaf"));
            normalSeeds += count(normal, ModItems.TEA_SEED);
            fortuneSeeds += count(fortune, ModItems.TEA_SEED);
        }
        helper.assertTrue(fortuneSeeds > normalSeeds,
                Component.literal("Fortune must increase mature tea seed drops"));
        helper.succeed();
    }

    @GameTest
    public void explosionsReduceTeaTreeDrops(GameTestHelper helper) {
        int normalCount = 0;
        int explosionCount = 0;
        for (long seed = 1; seed <= SAMPLES; seed++) {
            var normal = drops(helper, TeaTreeBlock.MAX_AGE, ItemStack.EMPTY, null, seed);
            var exploded = drops(helper, TeaTreeBlock.MAX_AGE, ItemStack.EMPTY, 4.0F, seed);
            normalCount += count(normal, ModItems.FRESH_TEA_LEAVES) + count(normal, ModItems.TEA_SEED);
            explosionCount += count(exploded, ModItems.FRESH_TEA_LEAVES) + count(exploded, ModItems.TEA_SEED);
        }
        helper.assertTrue(explosionCount < normalCount,
                Component.literal("Explosion decay must reduce tea tree drops"));
        helper.succeed();
    }

    private static List<ItemStack> drops(GameTestHelper helper, int age, ItemStack tool,
                                        Float explosionRadius, long seed) {
        var level = helper.getLevel();
        var table = level.getServer().reloadableRegistries().getLootTable(TEA_TREE_LOOT);
        var params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.ZERO)
                .withParameter(LootContextParams.BLOCK_STATE,
                        ModBlocks.TEA_TREE.defaultBlockState().setValue(TeaTreeBlock.AGE, age))
                .withParameter(LootContextParams.TOOL, tool)
                .withOptionalParameter(LootContextParams.EXPLOSION_RADIUS, explosionRadius)
                .create(LootContextParamSets.BLOCK);
        return table.getRandomItems(params, seed);
    }

    private static int count(List<ItemStack> drops, Item item) {
        int total = 0;
        for (var stack : drops) {
            if (stack.is(item)) {
                total += stack.getCount();
            }
        }
        return total;
    }
}
