package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.client.WalkmanMovingSound;
import com.ashindigo.musicexpansion.inventory.WalkmanInventory;
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

    public static void playTrack(ItemStack stack) {
        if (mc.player != null) {
            WalkmanInventory inv = ItemWalkman.getInventory(stack, mc.player.inventory);
            if (!isPlaying || !mc.getSoundManager().isPlaying(sound)) {
                ItemStack disc = inv.getStack(ItemWalkman.getSelectedSlot(stack)); // TODO Make this use getDiscInSlot(stack, slot)?
                if (!disc.isEmpty() && disc.getItem() instanceof MusicDiscItem) {
                    MusicDiscItem currentDisc = (MusicDiscItem) disc.getItem();
                    mc.inGameHud.setRecordPlayingOverlay(currentDisc.getDescription());
                    sound = new WalkmanMovingSound(currentDisc.getSound(), mc.player);
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


    public static MusicDiscItem getDiscInSlot(ItemStack stack, int slot) {
        ItemStack discStack = ItemStack.EMPTY;
        if (mc.player != null && stack.getTag() != null && stack.getTag().contains("Items")) {
            discStack = ItemWalkman.getInventory(stack, mc.player.inventory).getStack(slot);
        }
        if (!discStack.isEmpty()) {
            return (MusicDiscItem) discStack.getItem();
        }
        return null;
    }
}
