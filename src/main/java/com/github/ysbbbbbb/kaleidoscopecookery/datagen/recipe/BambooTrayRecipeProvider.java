package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.BambooTrayRecipeBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BambooTrayRecipeProvider extends ModRecipeProvider {
    private static final int DRYING_DURATION = 90 * 20;
    private static final int WETTING_DURATION = 45 * 20;

    public BambooTrayRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        BambooTrayRecipeBuilder.drying()
                .setIngredient(ModItems.FRESH_TEA_LEAVES)
                .setResult(ModItems.DRIED_TEA_LEAVES)
                .setDuration(DRYING_DURATION)
                .save(consumer, "fresh_tea_leaves_to_dried_tea_leaves");
        BambooTrayRecipeBuilder.drying()
                .setIngredient(Items.ROTTEN_FLESH)
                .setResult(Items.LEATHER)
                .setDuration(DRYING_DURATION)
                .save(consumer, "rotten_flesh_to_leather");
        BambooTrayRecipeBuilder.drying()
                .setIngredient(Blocks.MUD)
                .setResult(Blocks.CLAY)
                .setDuration(DRYING_DURATION)
                .save(consumer, "mud_to_clay");
        BambooTrayRecipeBuilder.drying()
                .setIngredient(Blocks.WET_SPONGE)
                .setResult(Blocks.SPONGE)
                .setDuration(DRYING_DURATION)
                .save(consumer, "wet_sponge_to_sponge");
        BambooTrayRecipeBuilder.drying()
                .setIngredient(Items.KELP)
                .setResult(Items.DRIED_KELP)
                .setDuration(DRYING_DURATION)
                .save(consumer, "kelp_to_dried_kelp");

        BambooTrayRecipeBuilder.wetting()
                .setIngredient(ModItems.DRIED_TEA_LEAVES)
                .setResult(ModItems.FRESH_TEA_LEAVES)
                .setDuration(WETTING_DURATION)
                .save(consumer, "dried_tea_leaves_to_fresh_tea_leaves");
        BambooTrayRecipeBuilder.wetting()
                .setIngredient(Items.LEATHER)
                .setResult(Items.ROTTEN_FLESH)
                .setDuration(WETTING_DURATION)
                .save(consumer, "leather_to_rotten_flesh");
        BambooTrayRecipeBuilder.wetting()
                .setIngredient(Blocks.CLAY)
                .setResult(Blocks.MUD)
                .setDuration(WETTING_DURATION)
                .save(consumer, "clay_to_mud");
        BambooTrayRecipeBuilder.wetting()
                .setIngredient(Blocks.SPONGE)
                .setResult(Blocks.WET_SPONGE)
                .setDuration(WETTING_DURATION)
                .save(consumer, "sponge_to_wet_sponge");
        BambooTrayRecipeBuilder.wetting()
                .setIngredient(Blocks.DIRT)
                .setResult(Blocks.MUD)
                .setDuration(WETTING_DURATION)
                .save(consumer, "dirt_to_mud");
        BambooTrayRecipeBuilder.wetting()
                .setIngredient(Blocks.COPPER_BLOCK.weathering().unaffected())
                .setResult(Blocks.COPPER_BLOCK.weathering().unaffected())
                .setDuration(WETTING_DURATION)
                .save(consumer, "copper_block_to_oxidized_copper");
        BambooTrayRecipeBuilder.wetting()
                .setIngredient(Items.DRIED_KELP)
                .setResult(Items.KELP)
                .setDuration(WETTING_DURATION)
                .save(consumer, "dried_kelp_to_kelp");

        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.white(), Blocks.CONCRETE.white(), "white");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.orange(), Blocks.CONCRETE.orange(), "orange");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.magenta(), Blocks.CONCRETE.magenta(), "magenta");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.lightBlue(), Blocks.CONCRETE.lightBlue(), "light_blue");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.yellow(), Blocks.CONCRETE.yellow(), "yellow");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.lime(), Blocks.CONCRETE.lime(), "lime");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.pink(), Blocks.CONCRETE.pink(), "pink");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.gray(), Blocks.CONCRETE.gray(), "gray");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.lightGray(), Blocks.CONCRETE.lightGray(), "light_gray");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.cyan(), Blocks.CONCRETE.cyan(), "cyan");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.purple(), Blocks.CONCRETE.purple(), "purple");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.blue(), Blocks.CONCRETE.blue(), "blue");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.brown(), Blocks.CONCRETE.brown(), "brown");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.green(), Blocks.CONCRETE.gray(), "green");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.red(), Blocks.CONCRETE.red(), "red");
        addConcretePowderRecipe(consumer, Blocks.CONCRETE_POWDER.black(), Blocks.CONCRETE.black(), "black");
    }

    private static void addConcretePowderRecipe(RecipeOutput consumer, Block powder, Block concrete, String color) {
        BambooTrayRecipeBuilder.wetting()
                .setIngredient(powder)
                .setResult(concrete)
                .setDuration(WETTING_DURATION)
                .save(consumer, color + "_concrete_powder_to_" + color + "_concrete");
    }
}
