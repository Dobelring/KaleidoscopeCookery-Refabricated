package com.github.ysbbbbbb.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.util.fluids.TeaFluidHelper;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public final class TeapotSoundGameTests implements FabricGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void bucketSoundsReachActingPlayerAndNearbyPlayerOnce(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        var server = helper.getLevel().getServer();
        var player = new ServerPlayer(server, helper.getLevel(),
                new GameProfile(UUID.randomUUID(), "tea-sound-actor"), ClientInformation.createDefault());
        var observer = new ServerPlayer(server, helper.getLevel(),
                new GameProfile(UUID.randomUUID(), "tea-sound-viewer"), ClientInformation.createDefault());
        var playerSounds = new SoundRecordingListener(player);
        var observerSounds = new SoundRecordingListener(observer);
        player.connection = playerSounds;
        observer.connection = observerSounds;
        // Register only for broadcasts, without triggering other mods' login handshakes.
        server.getPlayerList().getPlayers().add(player);
        server.getPlayerList().getPlayers().add(observer);
        try {
            player.setGameMode(GameType.SURVIVAL);
            var location = Vec3.atCenterOf(helper.absolutePos(pos));
            player.setPos(location);
            observer.setPos(location);
            for (Item bucket : new Item[]{Items.WATER_BUCKET, Items.LAVA_BUCKET, Items.MILK_BUCKET}) {
                helper.setBlock(pos, ModBlocks.TEAPOT);
                TeapotBlockEntity teapot = helper.getBlockEntity(pos);
                playerSounds.clearSounds();
                observerSounds.clearSounds();
                player.setItemInHand(InteractionHand.MAIN_HAND, bucket.getDefaultInstance());
                helper.assertTrue(teapot.addTeaFluid(helper.getLevel(), player, player.getMainHandItem()),
                        "Bucket failed to fill teapot");
                var emptySound = bucket == Items.LAVA_BUCKET ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
                helper.assertTrue(playerSounds.receivedExactly(emptySound),
                        "Wrong pouring sounds for " + bucket + ": " + playerSounds.describeSounds());
                helper.assertTrue(observerSounds.receivedExactly(emptySound), "Nearby player did not receive one pouring sound");

                playerSounds.clearSounds();
                observerSounds.clearSounds();
                player.setItemInHand(InteractionHand.MAIN_HAND, bucket.getDefaultInstance());
                helper.assertFalse(teapot.addTeaFluid(helper.getLevel(), player, player.getMainHandItem()),
                        "Full teapot accepted more fluid");
                helper.assertTrue(playerSounds.isSilent() && observerSounds.isSilent(), "Failed transfer played a sound");

                player.setItemInHand(InteractionHand.MAIN_HAND, Items.BUCKET.getDefaultInstance());
                helper.assertTrue(teapot.removeTeaFluid(helper.getLevel(), player, player.getMainHandItem()),
                        "Bucket failed to drain teapot");
                var fillSound = bucket == Items.LAVA_BUCKET ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
                helper.assertTrue(playerSounds.receivedExactly(fillSound), "Acting player did not receive one filling sound");
                helper.assertTrue(observerSounds.receivedExactly(fillSound), "Nearby player did not receive one filling sound");
            }

            playerSounds.clearSounds();
            observerSounds.clearSounds();
            TeaFluidHelper.playEmptySound(player, ResourceLocation.fromNamespaceAndPath("test", "virtual_liquid"));
            helper.assertTrue(playerSounds.receivedExactly(SoundEvents.BUCKET_EMPTY)
                    && observerSounds.receivedExactly(SoundEvents.BUCKET_EMPTY), "Virtual fluid pouring sound is missing");
        } finally {
            server.getPlayerList().getPlayers().remove(player);
            server.getPlayerList().getPlayers().remove(observer);
        }
        helper.succeed();
    }
}
