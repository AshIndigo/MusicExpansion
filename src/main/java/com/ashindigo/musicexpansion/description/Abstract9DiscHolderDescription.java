package com.ashindigo.musicexpansion.description;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import com.ashindigo.musicexpansion.inventory.Generic9DiscInventory;
import com.ashindigo.musicexpansion.item.Abstract9DiscItem;
import com.ashindigo.musicexpansion.item.CustomDiscItem;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class Abstract9DiscHolderDescription extends SyncedGuiDescription {

    public final ItemStack holder;
    public final UUID uuid;
    WGridPanel root;
    WSprite selected = new WSprite(new Identifier(MusicExpansion.MODID, "textures/misc/selected.png"));

    public Abstract9DiscHolderDescription(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInv, int hand) {
        super(type, syncId, playerInv);
        root = new WGridPanel();
        holder = playerInv.player.getStackInHand(Hand.values()[hand]);
        uuid = DiscHolderHelper.getUUID(holder);
        Generic9DiscInventory discHolderInv = DiscHolderHelper.getInventory(holder, playerInv);
        discHolderInv.addListener(sender -> {
            if (!playerInv.player.world.isClient) {
//              Set the items tag in inventory, by getting the tag and setting the "Items" tag to the resulting ListTag from Inventories.toTag() using the stacks from the current inventory
                CompoundTag invTag = Inventories.toTag(holder.getTag(), discHolderInv.getStacks());
                if (invTag != null) {
                    holder.getOrCreateTag().put("Items", invTag.getList("Items", 10));
                    playerInv.markDirty();
                }
            }
        });
        root.add(selected, 0, 1);
        root.add(new WItemSlot(discHolderInv, 0, 9, 1, false).setFilter(stack -> stack.getItem() instanceof MusicDiscItem || stack.getItem() instanceof CustomDiscItem), 0, 1);
        root.add(new WButton(new LiteralText("▶")).setOnClick(() -> ((Abstract9DiscItem) holder.getItem()).playSelectedDisc(playerInv.getStack(DiscHolderHelper.getSlotFromUUID(playerInv, uuid)))), 0, 2);
        root.add(new WButton(new LiteralText("⏹")).setOnClick(() -> ((Abstract9DiscItem) holder.getItem()).stopSelectedDisc(playerInv.getStack(DiscHolderHelper.getSlotFromUUID(playerInv, uuid)))), 2, 2);
        root.add(new WButton(new LiteralText("⏮")).setOnClick(() -> changeSelectedSlot(Math.max(0, DiscHolderHelper.getSelectedSlot(playerInv.getStack(DiscHolderHelper.getSlotFromUUID(playerInv, uuid))) - 1))), 4, 2);
        root.add(new WButton(new LiteralText("⏭")).setOnClick(() -> changeSelectedSlot(Math.min(8, DiscHolderHelper.getSelectedSlot(playerInv.getStack(DiscHolderHelper.getSlotFromUUID(playerInv, uuid))) + 1))), 6, 2);
        root.add(new WButton(new LiteralText("?")).setOnClick(() -> changeSelectedSlot(playerInv.player.getRandom().nextInt(9))), 8, 2);
        //root.add(new WLabeledSlider(0, 100, Axis.HORIZONTAL, new TranslatableText("text.musicexpansion.volume")), 1, 3); // fuck sliders all my homies hate sliders
        root.add(new WPlayerInvPanel(playerInventory, true), 0, 4);
        selected.setLocation(DiscHolderHelper.getSelectedSlot(holder) * 18, 18);
        setRootPanel(root);
        root.validate(this);
    }

    void changeSelectedSlot(int slot) {
        DiscHolderHelper.setSelectedSlot(slot, DiscHolderHelper.getSlotFromUUID(playerInventory, uuid));
        selected.setLocation(slot * 18, 18);
    }
}
