package com.github.ysbbbbbb.kaleidoscopecookery.compat.appleskin;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import squeek.appleskin.api.AppleSkinApi;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.food.FoodValues;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class AppleSkinCompat implements AppleSkinApi {
    private static final FoodValues EMPTY = new FoodValues(0, 0);
    private CompoundTag cachedContents;
    private FoodValues cachedFood = EMPTY;

    @Override
    public void registerEvents() {
        FoodValuesEvent.EVENT.register(this::onFoodValues);
        KaleidoscopeCookery.LOGGER.info("Registered AppleSkin lunch bag food preview");
    }

    void onFoodValues(FoodValuesEvent event) {
        if (!(event.itemStack.getItem() instanceof TransmutationLunchBagItem)) return;
        FoodValues food = getFoodValues(event.itemStack);
        event.defaultFoodValues = food;
        event.modifiedFoodValues = food;
    }

    FoodValues getFoodValues(ItemStack bag) {
        CompoundTag contents = bag.getTagElement("Items");
        if (!Objects.equals(contents, cachedContents)) {
            FoodProperties food = TransmutationLunchBagItem.getNextFoodProperties(bag);
            cachedFood = food == null ? EMPTY : new FoodValues(food.getNutrition(), food.getSaturationModifier());
            cachedContents = contents == null ? null : contents.copy();
        }
        return cachedFood;
    }
}
