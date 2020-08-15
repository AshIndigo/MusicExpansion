package com.ashindigo.musicexpansion.helpers;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.accessor.MusicDiscItemAccessor;
import com.ashindigo.musicexpansion.inventory.Generic9DiscInventory;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;
import java.util.UUID;

public class DiscHolderHelper {

    public static final SoundEvent MISSING_EVENT = new SoundEvent(new Identifier(MusicExpansion.MODID, "missing"));
    private static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static Generic9DiscInventory getInventory(ItemStack stack, PlayerInventory inv) {
        if (!stack.getOrCreateTag().contains("Items")) {
            // TODO, delete the check?
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
            return MathHelper.clamp(tag.getInt("selected"), 0, 8);
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
        ClientSidePacketRegistry.INSTANCE.sendToServer(MusicExpansion.CHANGE_SLOT_PACKET, buf);
    }

    /**
     * Finds the last specified disc holder in a players inventory
     * @param inventory The {@link PlayerInventory} to search
     * @return The slot number of the walkman if found, otherwise -1
     */
    public static int getActiveDiscHolderSlot(PlayerInventory inventory) {
        int slot = -1;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (isActive(stack)) {
                slot = i;
            }
        }
        return slot;
    }


    /**
     * Check to see if the disc holder in the given inv has the specified event
     * @param event The event to check
     * @param inv The player inventory to check
     * @return true if the walkman has a disc that can play the sound event, false if not
     */
    public static boolean discHolderContainsSound(SoundEvent event, PlayerInventory inv, UUID uuid) {
        ItemStack stack = inv.getStack(getSlotFromUUID(inv, uuid));
        Generic9DiscInventory discInv = getInventory(stack, inv);
        for (int i = 0; i < discInv.size(); i++) {
            ItemStack disc = discInv.getStack(i);
            if (disc.getItem() instanceof CustomDiscItem) {
                Optional<SoundEvent> opt = DiscHelper.getSetTrack(disc);
                if (opt.orElse(MISSING_EVENT).getId().equals(event.getId())) {
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

    public static int getSlotFromUUID(PlayerInventory inv, UUID uuid) {
        for (int i = 0; i < inv.size(); i++) {
            if (getUUID(inv.getStack(i)).equals(uuid)) {
               return i;
            }
        }
        return -1;
    }

    public static ItemStack getDiscInSlot(ItemStack stack, int slot) {
        ItemStack discStack = ItemStack.EMPTY;
        if (MusicHelper.mc.player != null && stack.getTag() != null && stack.getTag().contains("Items")) {
            discStack = getInventory(stack, MusicHelper.mc.player.inventory).getStack(slot);
        }
        return discStack;
    }

    public static UUID getUUID(ItemStack stack) {
        UUID uuid = EMPTY_UUID;
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("uuid")) {
            uuid = UUID.fromString(tag.getString("uuid"));
        }
        return uuid;
    }

    public static void setupInitialTags(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("uuid")) {
            tag.putString("uuid", UUID.randomUUID().toString());
        }
        if (!tag.contains("selected")) {
            tag.putInt("selected", 0);
        }
        if (!tag.contains("active")) {
            tag.putBoolean("active", false);
        }
        if (!tag.contains("Items")) {
            CompoundTag invTag = Inventories.toTag(tag, new Generic9DiscInventory().getStacks());
            tag.put("Items", invTag.getList("Items", 10));
        }
        if (!tag.contains("volume")) {
            tag.putFloat("volume", 1.0F);
        }
        stack.setTag(tag);
    }

    // TODO Will offhand be an issue?
    public static boolean containsUUID(UUID uuid, PlayerInventory inventory) {
        for (ItemStack stack : inventory.main) {
            if (getUUID(stack).equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public static void toggleActive(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("active", !tag.getBoolean("active"));
        stack.setTag(tag);
    }

    public static boolean isActive(ItemStack stack) {
        boolean active = false;
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("active")) {
            active = tag.getBoolean("active");
        }
        return active;
    }

    public static float getVolume(ItemStack stack) {
        return stack.getOrCreateTag().getFloat("volume");
    }
}
