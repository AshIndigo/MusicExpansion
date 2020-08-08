package com.ashindigo.musicexpansion.helpers;

import com.ashindigo.musicexpansion.client.WalkmanMovingSound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.ItemStack;

/**
 * Helper methods for music or walkman related functions
 * Client only.
 */
// TODO Stop code reuse!
public class MusicHelper {

    static final MinecraftClient mc = MinecraftClient.getInstance();
    static WalkmanMovingSound sound;
    static boolean isPlaying;

    public static void playWalkmanTrack(ItemStack walkman) {
        if (mc.player != null) {
            if (!isPlaying || !mc.getSoundManager().isPlaying(sound)) {
                ItemStack disc = DiscHolderHelper.getDiscInSlot(walkman, DiscHolderHelper.getSelectedSlot(walkman));
                if (!disc.isEmpty()) {
                    mc.inGameHud.setRecordPlayingOverlay(DiscHelper.getDesc(disc));
                    sound = new WalkmanMovingSound(DiscHelper.getEvent(disc), mc.player);
                    mc.getSoundManager().play(sound);
                    isPlaying = true;
                }
            }
        }
    }

    // TODO Rewrite
    public static void stopTrack() {
        mc.getSoundManager().stop(sound);
        isPlaying = false;
    }

    public static void playBoomboxTrack(ItemStack stack) {

    }

    public static void playTrack(ItemStack stack, SoundInstance instance) {

    }
}
