package com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

@Environment(EnvType.CLIENT)
public class BambooTrayBlockEntityRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState[] items = new ItemStackRenderState[4];
    public final int[] counts = new int[4];
}
