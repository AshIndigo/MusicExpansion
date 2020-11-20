package com.ashindigo.musicexpansion.description;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import com.ashindigo.musicexpansion.inventory.Generic9DiscInventory;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class Abstract9DiscHolderDescription extends SyncedGuiDescription {

    public final ItemStack holder;
    public final UUID uuid;

    public Abstract9DiscHolderDescription(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInv, int hand) {
        super(type, syncId, playerInv);
        WGridPanel root = new WGridPanel();
        holder = playerInv.player.getStackInHand(Hand.values()[hand]);
        uuid = DiscHolderHelper.getUUID(holder);
        Generic9DiscInventory discHolderInv = DiscHolderHelper.getInventory(holder, playerInv);
        discHolderInv.addListener(sender -> {
            if (!playerInv.player.world.isClient) {
//              Set the items tag in inventory, by getting the tag and setting the "Items" tag to the resulting ListTag from Inventories.toTag() using the stacks from the current inventory
                CompoundTag invTag = Inventories.toTag(holder.getTag(), discHolderInv.getStacks());
                if (invTag != null) {
                    holder.getOrCreateTag().put("Items",  invTag.getList("Items", 10));
                    playerInv.markDirty();
                }
            }
        });
        root.add(new WItemSlot(discHolderInv, 0, 9, 1, false), 0, 1);
        root.add(new WSprite(new Identifier(MusicExpansion.MODID, "textures/misc/selected.png")), 0, 1);
        root.add(new WButton(new LiteralText("▶")), 0, 2);
        root.add(new WButton(new LiteralText("⏹")), 2, 2);
        root.add(new WButton(new LiteralText("⏮")), 4, 2);
        root.add(new WButton(new LiteralText("⏭")), 6, 2);
        root.add(new WButton(new LiteralText("?")), 8, 2);
        root.add(new WLabeledSlider(0, 100, Axis.HORIZONTAL, new TranslatableText("text.musicexpansion.volume")), 1, 3);
        root.add(new WPlayerInvPanel(playerInventory, true), 0, 4);
        setRootPanel(root);
        root.validate(this);
    }
}
