package com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class PatternModel extends Model<PatternModel.State> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pattern"), "main");

    private final ModelPart bone;
    private final ModelPart back;
    private final ModelPart front;

    public PatternModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.bone = root.getChild("bone");
        this.back = this.bone.getChild("back");
        this.front = this.bone.getChild("front");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition bone = root.addOrReplaceChild("bone", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
        bone.addOrReplaceChild("back", CubeListBuilder.create()
                .texOffs(0, 16).addBox(0.0F, 0.0F, -16.0F, 0.0F, 48.0F, 32.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.25F, -65.0F, 0.0F));
        bone.addOrReplaceChild("front", CubeListBuilder.create()
                .texOffs(0, -32).addBox(0.0F, 0.0F, -16.0F, 0.0F, 48.0F, 32.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.25F, -65.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 96);
    }

    public void setWaveAngle(float angle) {
        this.front.zRot = angle;
        this.back.zRot = angle;
    }

    @Override
    public void setupAnim(@NonNull State state) {
    }

    public record State() {
    }
}
