package com.ashindigo.musicexpansion.helpers;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.accessor.MusicDiscItemAccessor;
import com.ashindigo.musicexpansion.inventory.Generic9DiscInventory;
import com.ashindigo.musicexpansion.item.Abstract9DiscItem;
import com.ashindigo.musicexpansion.item.CustomDiscItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.DefaultedList;

public class DiscHolderHelper {
    public static Generic9DiscInventory getInventory(ItemStack stack, PlayerInventory inv) {
        if (!stack.getOrCreateTag().contains("Items")) {
            if (!inv.player.world.isClient) { // Set up inventory tag if needed, and copy over the selected slot int
                int slot = getSelectedSlot(stack);
                stack.setTag(Inventories.toTag(stack.getTag(), new Generic9DiscInventory().getStacks()));
                CompoundTag tag = stack.getOrCreateTag();
                tag.putInt("selected", slot);
                stack.setTag(tag);
                inv.markDirty();
            }
        }
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(9, ItemStack.EMPTY);
        Inventories.fromTag(stack.getOrCreateTag(), stacks);
        return new Generic9DiscInventory(stacks);
    }

    public static int getSelectedSlot(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("selected")) {
            return tag.getInt("selected");
        } else {
            tag.putInt("selected", 0);
            stack.setTag(tag);
            return 0;
        }
    }

    public static void setSelectedSlot(int slot, int invSlot) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(slot);
        buf.writeInt(invSlot);
        ClientSidePacketRegistry.INSTANCE.sendToServer(MusicExpansion.CHANGESLOT_PACKET, buf);
    }

    /**
     * Finds the last specified disc holder in a players inventory
     * @param inventory The {@link PlayerInventory} to search
     * @return The slot number of the walkman if found, otherwise -1
     */
    public static int getDiscHolderSlot(Class<? extends Abstract9DiscItem> clazz, PlayerInventory inventory) {
        int slot = -1;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem().getClass().isAssignableFrom(clazz)) {
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
    public static boolean discHolderContainsSound(Class<? extends Abstract9DiscItem> clazz, SoundEvent event, PlayerInventory inv) {
        ItemStack stack = inv.getStack(getDiscHolderSlot(clazz, inv));
        Generic9DiscInventory walkmanInv = getInventory(stack, inv);
        for (int i = 0; i < walkmanInv.size(); i++) {
            ItemStack disc = walkmanInv.getStack(i);
            if (disc.getItem() instanceof CustomDiscItem) {
                if (DiscHelper.getSetTrack(disc).getId().equals(event.getId())) {
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

    public static int getDiscHoldersInInv(Class<? extends Abstract9DiscItem> clazz, PlayerInventory inventory) {
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem().getClass().isAssignableFrom(clazz)) {
                count++;
            }
        }
        return count;
    }

    public static ItemStack getDiscInSlot(ItemStack stack, int slot) {
        ItemStack discStack = ItemStack.EMPTY;
        if (MusicHelper.mc.player != null && stack.getTag() != null && stack.getTag().contains("Items")) {
            discStack = getInventory(stack, MusicHelper.mc.player.inventory).getStack(slot);
        }
        return discStack;
    }
}
