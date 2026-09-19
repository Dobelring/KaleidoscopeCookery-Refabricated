package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.function.IntFunction;

public class BambooTrayRecipe extends SingleItemRecipe {
    private final Subtype subtype;
    private final int duration;

    public BambooTrayRecipe(Ingredient ingredient, ItemStackTemplate result, Subtype subtype, int duration) {
        super(BaseRecipe.NO_INFO, ingredient, result);
        this.subtype = subtype;
        this.duration = Math.max(duration, 1);
    }

    @Override
    public boolean matches(SingleRecipeInput input, @NonNull Level level) {
        return this.input().test(input.getItem(0));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NonNull String group() {
        return "bamboo_tray";
    }

    @Override
    public @NonNull RecipeSerializer<? extends SingleItemRecipe> getSerializer() {
        return ModRecipes.BAMBOO_TRAY_SERIALIZER;
    }

    @Override
    public @NonNull RecipeType<? extends SingleItemRecipe> getType() {
        return ModRecipes.BAMBOO_TRAY_RECIPE;
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return ModRecipes.BAMBOO_TRAY_CATEGORY;
    }

    public Ingredient getIngredient() {
        return this.input();
    }

    public ItemStackTemplate getResult() {
        return this.result();
    }

    public Subtype getSubtype() {
        return subtype;
    }

    public int getDuration() {
        return duration;
    }

    public enum Subtype implements StringRepresentable {
        WETTING(0, "wetting"),
        DRYING(1, "drying");

        private static final IntFunction<Subtype> BY_ID = ByIdMap.continuous(
                Subtype::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Subtype> CODEC = StringRepresentable.fromEnum(Subtype::values);
        public static final StreamCodec<ByteBuf, Subtype> STREAM_CODEC =
                ByteBufCodecs.idMapper(BY_ID, Subtype::getId);

        private final int id;
        private final String serializedName;

        Subtype(int id, String serializedName) {
            this.id = id;
            this.serializedName = serializedName;
        }

        private int getId() {
            return id;
        }

        @Override
        public @NonNull String getSerializedName() {
            return serializedName;
        }
    }
}
