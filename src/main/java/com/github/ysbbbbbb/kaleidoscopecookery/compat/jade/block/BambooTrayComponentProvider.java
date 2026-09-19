package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.BambooTrayBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum BambooTrayComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof BambooTrayBlockEntity tray)) {
            return;
        }
        for (int slot = 0; slot < tray.getContainerSize(); slot++) {
            ItemStack stack = tray.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            IElement icon = IElementHelper.get().smallItem(stack);
            tooltip.add(icon);
            tooltip.append((IElementHelper.get().spacer(2, 1)));
            tooltip.append(stack.getHoverName());
            tooltip.append(Component.literal(" " + tray.getProgressPercent(slot) + "%"));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ModPlugin.CHOPPING_BOARD;
    }
}
