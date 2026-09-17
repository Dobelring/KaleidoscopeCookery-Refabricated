package com.github.ysbbbbbb.kaleidoscopecookery;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public final class TeaUpdateSmokeScreen extends Screen {
    private final Item[] items;

    public TeaUpdateSmokeScreen(Item[] items) {
        super(Component.literal("Tea update asset smoke test"));
        this.items = items;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xFF202020);
        graphics.drawString(font, title, 12, 12, 0xFFFFFFFF);
        int columns = 5;
        int cellWidth = width / columns;
        for (int i = 0; i < items.length; i++) {
            int x = i % columns * cellWidth + cellWidth / 2;
            int y = 42 + i / columns * 52;
            var stack = items[i].getDefaultInstance();
            graphics.pose().pushPose();
            graphics.pose().translate(x - 16, y, 0);
            graphics.pose().scale(2, 2, 2);
            graphics.renderItem(stack, 0, 0);
            graphics.pose().popPose();
            String label = font.plainSubstrByWidth(stack.getHoverName().getString(), cellWidth - 6);
            graphics.drawString(font, label, x - font.width(label) / 2, y + 35, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
