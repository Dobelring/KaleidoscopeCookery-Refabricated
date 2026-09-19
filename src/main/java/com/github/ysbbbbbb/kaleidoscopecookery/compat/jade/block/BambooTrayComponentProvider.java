package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.BambooTrayBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModJadePlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.JadeUI;

public enum BambooTrayComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(@NonNull ITooltip tooltip, BlockAccessor accessor, @NonNull IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof BambooTrayBlockEntity tray)) {
            return;
        }

        for (int slot = 0; slot < tray.getContainerSize(); slot++) {
            ItemStack stack = tray.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            tooltip.add(JadeUI.smallItem(stack));
            tooltip.append(JadeUI.spacer(2, 1));
            tooltip.append(stack.getHoverName());
            tooltip.append(Component.literal(" " + tray.getProgressPercent(slot) + "%"));
        }
    }

    @Override
    public @NonNull Identifier getUid() {
        return ModJadePlugin.BAMBOO_TRAY;
    }
}
