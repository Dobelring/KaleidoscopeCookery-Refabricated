package com.github.ysbbbbbb.kaleidoscopecookery.item.template;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class WithTooltipsItem extends Item {
    protected final String key;

    public WithTooltipsItem(Properties properties, String name) {
        super(properties);
        this.key = "tooltip.kaleidoscope_cookery." + name;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }
}
