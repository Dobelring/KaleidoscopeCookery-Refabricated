package com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.BambooTrayRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public final class BambooTrayRecipeCategory implements IRecipeCategory<RecipeHolder<BambooTrayRecipe>> {
    public static final IRecipeHolderType<BambooTrayRecipe> TYPE = IRecipeType.create(ModRecipes.BAMBOO_TRAY_RECIPE);
    private static final Identifier BG = Identifier.fromNamespaceAndPath(
            KaleidoscopeCookery.MOD_ID, "textures/gui/jei/bamboo_tray.png");
    private static final MutableComponent TITLE = Component.translatable(
            "block.kaleidoscope_cookery.bamboo_tray");

    public static final int WIDTH = 176;
    public static final int HEIGHT = 78;

    private final IDrawable background;
    private final IDrawable icon;

    public BambooTrayRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BG, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemLike(ModItems.BAMBOO_TRAY);
    }

    @Override
    public void draw(RecipeHolder<BambooTrayRecipe> holder, @NonNull IRecipeSlotsView slots,
                     @NonNull GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
        BambooTrayRecipe recipe = holder.value();
        Component process = Component.translatable(
                "jei.kaleidoscope_cookery.bamboo_tray." + recipe.getSubtype().getSerializedName(),
                recipe.getDuration() / 20);
        Font font = Minecraft.getInstance().font;
        FormattedCharSequence sequence = process.getVisualOrderText();
        graphics.text(font, sequence, WIDTH / 2 - font.width(sequence) / 2, 68, 0x555555, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BambooTrayRecipe> holder,
                          @NonNull IFocusGroup focuses) {
        BambooTrayRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 41, 27)
                .add(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 29)
                .add(recipe.getResult().create());
    }

    @Override
    public @NonNull IRecipeHolderType<BambooTrayRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    @Nullable
    public IDrawable getIcon() {
        return icon;
    }
}
