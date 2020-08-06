package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.client.WalkmanMovingSound;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;

/**
 * Helper methods for music or walkman related functions
 * Client only.
 */
public class MusicHelper {

    static final MinecraftClient mc = MinecraftClient.getInstance();
    static WalkmanMovingSound sound;
    static boolean isPlaying;

    public static void playTrack(ItemStack walkman) {
        if (mc.player != null) {
            if (!isPlaying || !mc.getSoundManager().isPlaying(sound)) {
                ItemStack disc = getDiscInSlot(walkman, ItemWalkman.getSelectedSlot(walkman));
                if (!disc.isEmpty()) {
                    mc.inGameHud.setRecordPlayingOverlay(DiscHelper.getDesc(disc));
                    sound = new WalkmanMovingSound(DiscHelper.getEvent(disc), mc.player);
                    mc.getSoundManager().play(sound);
                    isPlaying = true;
                }
            }
        }
    }



    public static void stopTrack() {
        mc.getSoundManager().stop(sound);
        isPlaying = false;
    }


    public static ItemStack getDiscInSlot(ItemStack stack, int slot) {
        ItemStack discStack = ItemStack.EMPTY;
        if (mc.player != null && stack.getTag() != null && stack.getTag().contains("Items")) {
            discStack = ItemWalkman.getInventory(stack, mc.player.inventory).getStack(slot);
        }
        return discStack;
    }
}
