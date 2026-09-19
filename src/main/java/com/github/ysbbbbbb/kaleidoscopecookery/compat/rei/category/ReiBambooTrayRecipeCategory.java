package com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.ReiUtil;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.BambooTrayRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ReiBambooTrayRecipeCategory
        implements DisplayCategory<ReiBambooTrayRecipeCategory.BambooTrayRecipeDisplay> {
    public static final CategoryIdentifier<BambooTrayRecipeDisplay> ID =
            CategoryIdentifier.of(KaleidoscopeCookery.MOD_ID, "plugin/bamboo_tray");
    private static final MutableComponent TITLE =
            Component.translatable("block.kaleidoscope_cookery.bamboo_tray");
    private static final Identifier BG = Identifier.fromNamespaceAndPath(
            KaleidoscopeCookery.MOD_ID, "textures/gui/jei/bamboo_tray.png");
    public static final int WIDTH = 176;
    public static final int HEIGHT = 78;

    @Override
    public CategoryIdentifier<? extends BambooTrayRecipeDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public List<Widget> setupDisplay(BambooTrayRecipeDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>(5);
        int startX = bounds.x;
        int startY = bounds.y;
        Component process = Component.translatable(
                "jei.kaleidoscope_cookery.bamboo_tray." + display.subtype.getSerializedName(),
                display.duration / 20);

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(BG, startX, startY, 0, 0, WIDTH, HEIGHT));
        widgets.add(Widgets.withTranslate(Widgets.createDrawableWidget(
                (graphics, mouseX, mouseY, delta) -> drawCenteredString(graphics, process)),
                startX, startY));
        widgets.add(Widgets.createSlot(new Point(startX + 41, startY + 27))
                .entries(display.getInputEntries().getFirst())
                .disableBackground()
                .markInput());
        widgets.add(Widgets.createSlot(new Point(startX + 131, startY + 29))
                .entries(display.getOutputEntries().getFirst())
                .disableBackground()
                .markOutput());
        return widgets;
    }

    private static void drawCenteredString(GuiGraphics graphics, Component text) {
        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, text, WIDTH / 2 - font.width(text) / 2, 68, 0x555555, false);
    }

    @Override
    public int getDisplayWidth(BambooTrayRecipeDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModItems.BAMBOO_TRAY);
    }

    public static void registerCategories(CategoryRegistry registry) {
        registry.add(new ReiBambooTrayRecipeCategory());
        registry.addWorkstations(ID, ReiUtil.ofItem(ModItems.BAMBOO_TRAY));
    }

    public static final class BambooTrayRecipeDisplay extends BasicDisplay {
        private final BambooTrayRecipe.Subtype subtype;
        private final int duration;

        public static final DisplaySerializer<BambooTrayRecipeDisplay> SERIALIZER =
                DisplaySerializer.of(
                        RecordCodecBuilder.mapCodec(instance -> instance.group(
                                Identifier.CODEC.fieldOf("location").forGetter(
                                        display -> display.getDisplayLocation()
                                                .orElse(Identifier.withDefaultNamespace("air"))),
                                EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(
                                        BambooTrayRecipeDisplay::getInputEntries),
                                EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(
                                        BambooTrayRecipeDisplay::getOutputEntries),
                                BambooTrayRecipe.Subtype.CODEC.fieldOf("subtype").forGetter(
                                        display -> display.subtype),
                                com.mojang.serialization.Codec.INT.fieldOf("duration").forGetter(
                                        display -> display.duration)
                        ).apply(instance, BambooTrayRecipeDisplay::new)),
                        StreamCodec.composite(
                                Identifier.STREAM_CODEC, display -> display.getDisplayLocation()
                                        .orElse(Identifier.withDefaultNamespace("air")),
                                EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                                BambooTrayRecipeDisplay::getInputEntries,
                                EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                                BambooTrayRecipeDisplay::getOutputEntries,
                                BambooTrayRecipe.Subtype.STREAM_CODEC,
                                display -> display.subtype,
                                ByteBufCodecs.VAR_INT,
                                display -> display.duration,
                                BambooTrayRecipeDisplay::new
                        ));

        public BambooTrayRecipeDisplay(Identifier location, List<EntryIngredient> inputs,
                                       List<EntryIngredient> outputs,
                                       BambooTrayRecipe.Subtype subtype, int duration) {
            super(inputs, outputs, Optional.of(location));
            this.subtype = subtype;
            this.duration = duration;
        }

        public BambooTrayRecipeDisplay(RecipeHolder<BambooTrayRecipe> holder) {
            this(holder.id().identifier(),
                    ReiUtil.ofIngredients(holder.value().getIngredient()),
                    ReiUtil.ofItemStacks(holder.value().getResult().create()),
                    holder.value().getSubtype(),
                    holder.value().getDuration());
        }

        @Override
        public CategoryIdentifier<? extends BambooTrayRecipeDisplay> getCategoryIdentifier() {
            return ID;
        }

        @Override
        public @NonNull DisplaySerializer<? extends Display> getSerializer() {
            return SERIALIZER;
        }
    }
}
