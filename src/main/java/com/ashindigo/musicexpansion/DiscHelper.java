package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.inventory.Generic9DiscInventory;
import com.ashindigo.musicexpansion.item.CustomDiscItem;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import com.ashindigo.musicexpansion.accessor.MusicDiscItemAccessor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Various helpers related to the new NBT discs, that I would rather not copy and paste repeatedly/retype constantly
 * Also has minor Walkman related helpers
 */
public class DiscHelper {

    /**
     * Get the {@link SoundEvent} this custom disc is set to
     *
     * @param stack The disc in question, needs tag data
     * @return The {@link SoundEvent} this disc is linked to, otherwise null if it doesn't exist
     */
    public static SoundEvent getSetTrack(ItemStack stack) {
        return Registry.SOUND_EVENT.get(getTrackID(stack));
    }

    /**
     * Get the {@link Identifier} for the track this disc is set to
     *
     * @param stack The stack to check
     * @return The {@link Identifier} for the set track
     */
    public static Identifier getTrackID(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("track")) {
            return Identifier.tryParse(tag.getString("track"));
        } else {
            return new Identifier(MusicExpansion.MODID, "missing");
        }
    }

    /**
     * Set's the disc's track
     *
     * @param stack The stack to modify
     * @param id    The {@link Identifier} of the {@link SoundEvent in question}
     */
    public static void setTrack(ItemStack stack, Identifier id) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("track", id.toString());
        stack.setTag(tag);
    }

    /**
     * Creates a {@link CustomDiscItem} with the specified track set
     *
     * @param id The {@link Identifier} of the {@link SoundEvent} to be set
     * @return The modified stack
     */
    public static ItemStack createCustomDisc(Identifier id) {
        ItemStack disc = new ItemStack(MusicExpansion.customDisc);
        setTrack(disc, id);
        return disc;
    }

    /**
     * Finds the last walkman in a players inventory
     * @param inventory The {@link PlayerInventory} to search
     * @return The slot number of the walkman if found, otherwise -1
     */
    public static int getWalkman(PlayerInventory inventory) {
        int slot = -1;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() instanceof ItemWalkman) {
                slot = i;
            }
        }
        return slot;
    }

    /**
     * Check to see if the Walkman in the given inv has the specified event
     * @param event The event to check
     * @param inv The player inventory to check
     * @return true if the walkman has a disc that can play the sound event, false if not
     */
    public static boolean walkmanContainsSound(SoundEvent event, PlayerInventory inv) {
        ItemStack stack = inv.getStack(getWalkman(inv));
        Generic9DiscInventory walkmanInv = ItemWalkman.getInventory(stack, inv);
        for (int i = 0; i < walkmanInv.size(); i++) {
            ItemStack disc = walkmanInv.getStack(i);
            if (disc.getItem() instanceof CustomDiscItem) {
                if (getSetTrack(disc).getId().equals(event.getId())) {
                    return true;
                }
            } else if (disc.getItem() instanceof MusicDiscItem) {
                if (((MusicDiscItemAccessor) disc.getItem()).musicexpansion_getSound().getId().equals(event.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the {@link SoundEvent} for a {@link CustomDiscItem} or a  {@link MusicDiscItem}
     * @param stack The disc stack to get the event of
     * @return The {@link SoundEvent} of the disc, or null if one doesn't exist
     */
    public static SoundEvent getEvent(ItemStack stack) {
        if (stack.getItem() instanceof MusicDiscItem) {
            return ((MusicDiscItemAccessor) stack.getItem()).musicexpansion_getSound();
        } else if (stack.getItem() instanceof CustomDiscItem) {
            return getSetTrack(stack);
        }
        return null;
    }

    /**
     * Get's the description for a {@link CustomDiscItem} or a  {@link MusicDiscItem}
     * @param stack The disc stack to get the description of
     * @return The {@link Text} description of the disc, typically the song name and author
     */
    // Client only because of getDescription()
    public static Text getDesc(ItemStack stack) {
        if (stack.getItem() instanceof MusicDiscItem) {
            return ((MusicDiscItem) stack.getItem()).getDescription();
        } else if (stack.getItem() instanceof CustomDiscItem) {
            return new TranslatableText("item." + getTrackID(stack).toString().replace(":", ".") + ".desc");
        }
        return new LiteralText("");
    }
}
