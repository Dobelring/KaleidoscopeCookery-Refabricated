package com.github.ysbbbbbb.kaleidoscopecookery.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.AnimationState;

import java.util.Arrays;
import java.util.List;

/**
 * Exercises deferred drawing's setupAnim calls without a GPU or world.
 */
public final class BlockEntityAnimationIsolationTest {
    public static void main(String[] args) {
        testTeapot();
        for (int animation = 0; animation < 5; animation++) {
            testTrashCan(animation);
        }
        System.out.println("Block entity animation isolation: all checks passed.");
    }

    private static void testTeapot() {
        TeapotModel model = new TeapotModel(TeapotModel.createBodyLayer().bakeRoot());
        AnimationState boiling = startedAnimation();
        for (int variant = 0; variant < 3; variant++) {
            TeapotModel.State idle = new TeapotModel.State(2.5F, new AnimationState(), variant);
            TeapotModel.State active = new TeapotModel.State(2.5F, boiling, variant);
            assertIsolated(model, active, idle, "teapot variant " + variant);
        }

        model.setupAnim(new TeapotModel.State(2.5F, boiling, 1));
        require(model.root().getChild("base").visible, "Based teapot lost its base");
        require(!model.root().getChild("chain").visible, "Based teapot inherited a chain");
        model.setupAnim(new TeapotModel.State(2.5F, new AnimationState(), 2));
        require(!model.root().getChild("base").visible, "Chained teapot inherited a base");
        require(model.root().getChild("chain").visible, "Chained teapot lost its chain");
        model.setupAnim(new TeapotModel.State(2.5F, new AnimationState(), 0));
        require(!model.root().getChild("base").visible && !model.root().getChild("chain").visible,
                "Common teapot inherited another variant's parts");
    }

    private static void testTrashCan(int animation) {
        TrashCanModel model = new TrashCanModel(TrashCanModel.createBodyLayer().bakeRoot());
        AnimationState[] states = new AnimationState[5];
        Arrays.setAll(states, ignored -> new AnimationState());
        states[animation].start(0);
        TrashCanModel.State active = new TrashCanModel.State(
                2.5F, states[0], states[1], states[2], states[3], states[4]);
        TrashCanModel.State idle = new TrashCanModel.State(2.5F,
                new AnimationState(), new AnimationState(), new AnimationState(),
                new AnimationState(), new AnimationState());
        assertIsolated(model, active, idle, "trash can animation " + animation);

        model.setupAnim(idle);
        float[] idlePose = snapshot(model);
        model.setupAnim(active);
        states[animation].stop();
        model.setupAnim(active);
        require(Arrays.equals(idlePose, snapshot(model)), "Stopped trash can retained its animation");
    }

    private static <S> void assertIsolated(Model<S> model, S active, S idle, String label) {
        model.setupAnim(idle);
        float[] idlePose = snapshot(model);
        model.setupAnim(active);
        float[] activePose = snapshot(model);
        require(!Arrays.equals(idlePose, activePose), label + ": test animation did not move");

        // The renderer can draw the same shared model repeatedly, in either order.
        for (int i = 0; i < 3; i++) {
            model.setupAnim(active);
            require(Arrays.equals(activePose, snapshot(model)), label + ": animation accumulated");
            model.setupAnim(idle);
            require(Arrays.equals(idlePose, snapshot(model)), label + ": idle instance inherited animation");
            model.setupAnim(active);
            require(Arrays.equals(activePose, snapshot(model)), label + ": render order changed animation");
        }
    }

    private static AnimationState startedAnimation() {
        AnimationState state = new AnimationState();
        state.start(0);
        return state;
    }

    private static float[] snapshot(Model<?> model) {
        List<ModelPart> parts = model.allParts();
        float[] pose = new float[parts.size() * 10];
        int index = 0;
        for (ModelPart part : parts) {
            pose[index++] = part.x;
            pose[index++] = part.y;
            pose[index++] = part.z;
            pose[index++] = part.xRot;
            pose[index++] = part.yRot;
            pose[index++] = part.zRot;
            pose[index++] = part.xScale;
            pose[index++] = part.yScale;
            pose[index++] = part.zScale;
            pose[index++] = part.visible ? 1 : 0;
        }
        return pose;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
