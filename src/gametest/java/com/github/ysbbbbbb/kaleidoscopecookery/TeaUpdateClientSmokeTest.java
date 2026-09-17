package com.github.ysbbbbbb.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TeaBannerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.BambooTrayBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.LeftBannerModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.NormalBannerModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.banner.PatternModel;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFluids;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.atomic.AtomicBoolean;

public final class TeaUpdateClientSmokeTest implements ClientModInitializer {
    private final AtomicBoolean screenshotSaved = new AtomicBoolean();
    private int ticks;
    private int previewTicks;
    private boolean previewStarted;

    @Override
    public void onInitializeClient() {
        if (Boolean.getBoolean("cookery.teaClientSmoke")) {
            ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        }
    }

    private void tick(Minecraft client) {
        if (++ticks > 1800) {
            throw new IllegalStateException("Tea client smoke test timed out");
        }
        if (!previewStarted && ticks > 40 && client.screen != null && client.getOverlay() == null) {
            var milk = BuiltInRegistries.FLUID.get(ModFluids.MILK_ID);
            var milkRenderer = FluidRenderHandlerRegistry.INSTANCE.get(milk);
            require(milkRenderer != null, "Missing milk fluid renderer");
            var sprites = milkRenderer.getFluidSprites(null, null, milk.defaultFluidState());
            require(sprites != null && sprites.length > 0
                            && !sprites[0].contents().name().equals(MissingTextureAtlasSprite.getLocation()),
                    "Missing milk fluid texture");
            Item[] items = {
                    ModItems.TEA_SEED, ModItems.FRESH_TEA_LEAVES, ModItems.DRIED_TEA_LEAVES,
                    ModItems.BARLEY_TEA_BAG, ModItems.BILUOCHUN_TEA_BAG, ModItems.OOLONG_TEA_BAG,
                    ModItems.TIEGUANYIN_TEA_BAG, ModItems.SAKURA_FUBUKI_TEA_BAG, ModItems.BUTTER_TEA_BAG,
                    ModItems.TEA_EGG, ModItems.CLAY_POT_MILK_TEA,
                    TeacupRegistry.getItem(TeacupRegistry.BUTTER_TEA), TeacupRegistry.getItem(TeacupRegistry.MYSTERY_TEA),
                    ModItems.BAMBOO_TRAY, ModItems.TEA_BANNER, ModItems.LONG_BENCH,
                    ModItems.EIGHT_IMMORTALS_TABLE, ModItems.RED_LANTERN, ModItems.TEAPOT
            };
            for (Item item : items) {
                require(client.getItemRenderer().getModel(item.getDefaultInstance(), null, null, 0)
                        != client.getModelManager().getMissingModel(), "Missing item model: " + item);
            }
            for (Block block : new Block[]{ModBlocks.TEA_TREE, ModBlocks.BAMBOO_TRAY, ModBlocks.TEA_BANNER,
                    ModBlocks.LONG_BENCH, ModBlocks.EIGHT_IMMORTALS_TABLE, ModBlocks.RED_LANTERN,
                    ModBlocks.CLAY_POT_MILK_TEA, TeacupRegistry.getBlock(TeacupRegistry.BUTTER_TEA),
                    TeacupRegistry.getBlock(TeacupRegistry.MYSTERY_TEA)}) {
                for (var state : block.getStateDefinition().getPossibleStates()) {
                    require(client.getBlockRenderer().getBlockModel(state) != client.getModelManager().getMissingModel(),
                            "Missing block model: " + state);
                }
            }
            client.getEntityModels().bakeLayer(NormalBannerModel.LAYER_LOCATION);
            client.getEntityModels().bakeLayer(LeftBannerModel.LAYER_LOCATION);
            client.getEntityModels().bakeLayer(PatternModel.LAYER_LOCATION);
            require(client.getBlockEntityRenderDispatcher().getRenderer(
                    new TeaBannerBlockEntity(BlockPos.ZERO, ModBlocks.TEA_BANNER.defaultBlockState())) != null,
                    "Missing tea banner renderer");
            require(client.getBlockEntityRenderDispatcher().getRenderer(
                    new BambooTrayBlockEntity(BlockPos.ZERO, ModBlocks.BAMBOO_TRAY.defaultBlockState())) != null,
                    "Missing bamboo tray renderer");
            client.setScreen(new TeaUpdateSmokeScreen(items));
            previewStarted = true;
        }
        if (previewStarted && ++previewTicks == 20) {
            Screenshot.grab(client.gameDirectory, "tea-update.png", client.getMainRenderTarget(), message -> {
                KaleidoscopeCookery.LOGGER.info("Tea smoke screenshot: {}", message.getString());
                screenshotSaved.set(true);
            });
        }
        if (screenshotSaved.get()) {
            KaleidoscopeCookery.LOGGER.info("TEA_CLIENT_SMOKE_PASSED");
            client.stop();
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
