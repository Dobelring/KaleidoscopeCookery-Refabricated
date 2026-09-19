package com.github.ysbbbbbb.kaleidoscopecookery.compat.rrv.bamboo_tray;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.BambooTrayRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class BambooTrayViewRecipe implements ReliableClientRecipe {
    private final Identifier id;
    private final SlotContent ingredient;
    private final SlotContent result;
    private final BambooTrayRecipe.Subtype subtype;
    private final int duration;

    public BambooTrayViewRecipe(Identifier id, Ingredient ingredient, ItemStackTemplate result,
                                BambooTrayRecipe.Subtype subtype, int duration) {
        this.id = id;
        this.ingredient = SlotContent.of(ingredient);
        this.result = SlotContent.of(result);
        this.subtype = subtype;
        this.duration = duration;
    }

    @Override
    public ReliableClientRecipeType getType() {
        return BambooTrayViewType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.ingredient);
        slotFillContext.bindSlot(1, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.ingredient);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {
        Component process = Component.translatable(
                "jei.kaleidoscope_cookery.bamboo_tray." + this.subtype.getSerializedName(),
                this.duration / 20
        );
        Font font = context.font();
        context.guiGraphics().text(font, process, 88 - font.width(process) / 2, 68, 0x555555, false);
    }

    public BambooTrayRecipe.Subtype getSubtype() {
        return this.subtype;
    }

    public int getDuration() {
        return this.duration;
    }
}
