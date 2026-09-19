package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.BambooTrayBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.BambooTrayBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BambooTrayBlockEntityRender implements BlockEntityRenderer<BambooTrayBlockEntity, BambooTrayBlockEntityRenderState> {
    private static final double[][] SLOT_POSITIONS = {
            {0.25, 0.25}, {0.75, 0.25}, {0.25, 0.75}, {0.75, 0.75}
    };

    private final ItemModelResolver itemModelResolver;

    public BambooTrayBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public @NonNull BambooTrayBlockEntityRenderState createRenderState() {
        return new BambooTrayBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull BambooTrayBlockEntity blockEntity,
                                   @NonNull BambooTrayBlockEntityRenderState state,
                                   float partialTick,
                                   @NonNull Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, crumblingOverlay);
        int seed = (int) blockEntity.getBlockPos().asLong();
        for (int slot = 0; slot < state.items.length; slot++) {
            ItemStackRenderState itemState = new ItemStackRenderState();
            int count = blockEntity.getItem(slot).getCount();
            state.counts[slot] = count;
            if (count > 0) {
                this.itemModelResolver.updateForTopItem(
                        itemState, blockEntity.getItem(slot), ItemDisplayContext.FIXED,
                        blockEntity.getLevel(), null, seed + slot);
            }
            state.items[slot] = itemState;
        }
    }

    @Override
    public void submit(@NonNull BambooTrayBlockEntityRenderState state,
                       @NonNull PoseStack poseStack,
                       @NonNull SubmitNodeCollector submitNodeCollector,
                       @NonNull CameraRenderState cameraRenderState) {
        for (int slot = 0; slot < state.items.length; slot++) {
            ItemStackRenderState itemState = state.items[slot];
            if (itemState == null || itemState.isEmpty()) {
                continue;
            }

            int count = state.counts[slot];
            int copies = count == 1 ? 1 : Math.min(5, (count - 1) / 16 + 2);
            RandomSource random = RandomSource.create((long) slot * 31L + 0x5EEDL);
            for (int copy = 0; copy < copies; copy++) {
                poseStack.pushPose();
                poseStack.translate(
                        SLOT_POSITIONS[slot][0] + (random.nextDouble() - 0.5) * 0.1,
                        0.14 + copy * 0.01 + slot * 0.005,
                        SLOT_POSITIONS[slot][1] + (random.nextDouble() - 0.5) * 0.1);
                poseStack.rotateDegrees(Axis.XP, 90);
                poseStack.rotateDegrees(Axis.ZP, random.nextFloat() * 360.0F);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                itemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }
        }
    }
}
