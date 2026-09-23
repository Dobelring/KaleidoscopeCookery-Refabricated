package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModArmorMaterials;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsItem;
import net.minecraft.world.item.equipment.ArmorType;

public class StrawHatItem extends WithTooltipsItem {

    private final boolean hasFlower;

    public StrawHatItem(boolean hasFlower, Properties properties) {
        super(properties.stacksTo(1).humanoidArmor(ModArmorMaterials.FARMER, ArmorType.HELMET), "straw_hat");
        this.hasFlower = hasFlower;
    }

    public boolean hasFlower() {
        return hasFlower;
    }
}
