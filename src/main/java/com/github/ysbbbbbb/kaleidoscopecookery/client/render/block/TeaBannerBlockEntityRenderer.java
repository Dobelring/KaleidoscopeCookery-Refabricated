package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.TeaBannerBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TeaBannerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.LeftBannerModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.NormalBannerModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.PatternModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.renderstate.TeaBannerBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TeaBannerBlockEntityRenderer implements BlockEntityRenderer<TeaBannerBlockEntity, TeaBannerBlockEntityRenderState> {
    private final NormalBannerModel normalBanner;
    private final LeftBannerModel wallBanner;
    private final PatternModel patternModel;

    public TeaBannerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.normalBanner = new NormalBannerModel(context.bakeLayer(NormalBannerModel.LAYER_LOCATION));
        this.wallBanner = new LeftBannerModel(context.bakeLayer(LeftBannerModel.LAYER_LOCATION));
        this.patternModel = new PatternModel(context.bakeLayer(PatternModel.LAYER_LOCATION));
    }

    @Override
    public @NonNull TeaBannerBlockEntityRenderState createRenderState() {
        return new TeaBannerBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull TeaBannerBlockEntity blockEntity,
                                   @NonNull TeaBannerBlockEntityRenderState state,
                                   float partialTick,
                                   @NonNull Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, crumblingOverlay);
        state.facing = blockEntity.getBlockState().getValue(TeaBannerBlock.FACING);
        state.face = blockEntity.getBlockState().getValue(TeaBannerBlock.FACE);
        state.color = blockEntity.getColor();
        state.patternItem = blockEntity.getPatternItem();
        state.animationTime = blockEntity.getBlockPos().getX() * 7L
                + blockEntity.getBlockPos().getY() * 9L
                + blockEntity.getBlockPos().getZ() * 13L
                + (blockEntity.getLevel() == null ? 0L : blockEntity.getLevel().getGameTime())
                + partialTick;
    }

    @Override
    public void submit(@NonNull TeaBannerBlockEntityRenderState state,
                       @NonNull PoseStack poseStack,
                       @NonNull SubmitNodeCollector submitNodeCollector,
                       @NonNull CameraRenderState cameraRenderState) {
        float waveAngle = (-0.0125F + 0.01F * Mth.cos(Mth.PI * 2.0F * state.animationTime / 100.0F)) * Mth.HALF_PI;
        this.normalBanner.setWaveAngle(waveAngle);
        this.wallBanner.setWaveAngle(waveAngle);
        this.patternModel.setWaveAngle(waveAngle);

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YN.rotationDegrees(180.0F - state.facing.get2DDataValue() * 90.0F));
        boolean wallMounted = state.face == AttachFace.WALL;
        if (wallMounted) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        }

        Identifier baseTexture = Identifier.fromNamespaceAndPath(
                KaleidoscopeCookery.MOD_ID, "textures/entity/banner/" + state.color.getName() + ".png");
        RenderType baseRenderType = RenderTypes.entityCutout(baseTexture);
        if (wallMounted) {
            submitNodeCollector.submitModel(wallBanner, new LeftBannerModel.State(), poseStack,
                    baseRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, null);
        } else {
            submitNodeCollector.submitModel(normalBanner, new NormalBannerModel.State(), poseStack,
                    baseRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, null);
        }

        String patternName = TeaBannerBlockEntity.getPatternTexture(state.patternItem);
        if (patternName == null) {
            patternName = "tea";
        }
        Identifier patternTexture = Identifier.fromNamespaceAndPath(
                KaleidoscopeCookery.MOD_ID, "textures/entity/pattern/" + patternName + ".png");
        if (wallMounted) {
            poseStack.translate(-1.0F, 3.0F, 0.0F);
        }
        submitNodeCollector.submitModel(patternModel, new PatternModel.State(), poseStack,
                RenderTypes.entityCutout(patternTexture), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, null);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}
