package com.ashindigo.musicexpansion.helpers;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.UUID;

/**
 * Helper methods for music or walkman related functions
 * Client only.
 */
public class MusicHelper {

    static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Map<UUID, SoundInstance> playingTracks = Maps.newHashMap();

    public static void stopTrack(UUID uuid) {
        mc.getSoundManager().stop(playingTracks.get(uuid));
        playingTracks.remove(uuid);
    }

    public static void stopTrack(ItemStack stack) {
       stopTrack(DiscHolderHelper.getUUID(stack));
    }
    
    public static void playTrack(ItemStack stack, SoundInstance instance) {
        if (mc.player != null) {
            if (!mc.getSoundManager().isPlaying(playingTracks.get(DiscHolderHelper.getUUID(stack)))) {
                ItemStack disc = DiscHolderHelper.getDiscInSlot(stack, DiscHolderHelper.getSelectedSlot(stack));
                if (!disc.isEmpty()) {
                    mc.inGameHud.setRecordPlayingOverlay(DiscHelper.getDesc(disc));
                    mc.getSoundManager().play(instance);
                    playingTracks.put(DiscHolderHelper.getUUID(stack), instance);
                }
            }
        }
    }
}
