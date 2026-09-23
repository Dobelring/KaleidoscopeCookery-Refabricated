package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class StockpotLidItem extends WithTooltipsItem {
    private static final int NORMAL = 0;
    private static final int USING = 1;

    public StockpotLidItem(Properties p) {
        super(p.durability(120), "stockpot_lid");
    }

    public @NonNull Component getName(final ItemStack itemStack) {
        DyeColor baseColor = itemStack.get(DataComponents.BASE_COLOR);
        if (baseColor != null) {
            return Component.translatable(this.descriptionId + "." + baseColor.getName());
        } else {
            return super.getName(itemStack);
        }
    }

    @SuppressWarnings("unused")
    @Deprecated
    public static float getTexture(ItemStack stack, Level level, LivingEntity entity, int seed) {
        if (entity != null && entity.isUsingItem() && entity.getUseItem() == stack) {
            return USING;
        }
        return NORMAL;
    }
}
