package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.TeapotContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeapotRecipeTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void milkRecipeRequiresCorrectFluidIngredientAndCount() {
        TeapotRecipe recipe = new TeapotRecipe(new ResourceLocation("test", "milk_tea"), ModFluids.MILK_ID,
                Ingredient.of(Items.SUGAR), 2, 240, new ItemStack(Items.POTION));
        assertTrue(recipe.matches(new TeapotContainer(new ItemStack(Items.SUGAR, 2), ModFluids.MILK_ID), null));
        assertFalse(recipe.matches(new TeapotContainer(new ItemStack(Items.SUGAR), ModFluids.MILK_ID), null));
        assertFalse(recipe.matches(new TeapotContainer(new ItemStack(Items.STICK, 2), ModFluids.MILK_ID), null));
        assertFalse(recipe.matches(new TeapotContainer(new ItemStack(Items.SUGAR, 2), new ResourceLocation("water")), null));
    }

    @Test
    void unrelatedAndUnknownFluidsDoNotMatchMilk() {
        assertFalse(ModFluids.matchesTeaFluid(ModFluids.MILK_ID, new ResourceLocation("lava")));
        assertFalse(ModFluids.matchesTeaFluid(ModFluids.MILK_ID, new ResourceLocation("test", "milk_imitation")));
        assertFalse(ModFluids.matchesTeaFluid(new ResourceLocation("water"), ModFluids.MILK_ID));
        assertTrue(ModFluids.matchesTeaFluid(new ResourceLocation("water"), new ResourceLocation("water")));
    }
}
