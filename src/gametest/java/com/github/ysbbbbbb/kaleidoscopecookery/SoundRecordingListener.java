package com.github.ysbbbbbb.kaleidoscopecookery;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;

final class SoundRecordingListener extends ServerGamePacketListenerImpl {
    private final List<SoundEvent> sounds = new ArrayList<>(8);

    SoundRecordingListener(ServerPlayer player) {
        super(player.server, new Connection(PacketFlow.SERVERBOUND), player,
                CommonListenerCookie.createInitial(player.getGameProfile(), false));
    }

    @Override
    public void send(Packet<?> packet) {
        if (packet instanceof ClientboundSoundPacket sound) {
            sounds.add(sound.getSound().value());
        }
    }

    boolean receivedExactly(SoundEvent expected) {
        return sounds.size() == 1 && sounds.getFirst() == expected;
    }

    void clearSounds() {
        sounds.clear();
    }

    boolean isSilent() {
        return sounds.isEmpty();
    }

    String describeSounds() {
        return sounds.stream().map(sound -> sound.getLocation().toString()).toList().toString();
    }
}
