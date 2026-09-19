package com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.AttachFace;

@Environment(EnvType.CLIENT)
public class TeaBannerBlockEntityRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public AttachFace face = AttachFace.FLOOR;
    public DyeColor color = DyeColor.RED;
    public ItemStack patternItem = ItemStack.EMPTY;
    public float animationTime;
}
