package com.ashindigo.musicexpansion.handler;

import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import com.ashindigo.musicexpansion.inventory.Generic9DiscInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Hand;
import spinnery.common.handler.BaseScreenHandler;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;

import java.util.UUID;

public abstract class Abstract9DiscHolderHandler extends BaseScreenHandler {

    public static final int INVENTORY = 1;
    public final ItemStack holder;
    public final UUID uuid;

    public Abstract9DiscHolderHandler(int syncId, PlayerInventory inv, int hand) {
        super(syncId, inv);
        WInterface mainInterface = getInterface();
        holder = inv.player.getStackInHand(Hand.values()[hand]);
        uuid = DiscHolderHelper.getUUID(holder);
        Generic9DiscInventory discHolderInv = DiscHolderHelper.getInventory(holder, inv);
        addInventory(INVENTORY, discHolderInv);
        discHolderInv.addListener(sender -> {
            if (!inv.player.world.isClient) {
//                 Set the items tag in inventory, by getting the tag and setting the "Items" tag to the resulting ListTag from Inventories.toTag() using the stacks from the current inventory
                CompoundTag invTag = Inventories.toTag(holder.getTag(), discHolderInv.getStacks());
                if (invTag != null) {
                    holder.getOrCreateTag().put("Items",  invTag.getList("Items", 10));
                    inv.markDirty();
                }
            }
        });
        for (int i = 0; i < 9; i++) {
            mainInterface.createChild(WSlot::new).setSlotNumber(i).setInventoryNumber(INVENTORY);
        }
        WSlot.addHeadlessPlayerInventory(mainInterface);
    }

    @Override
    public abstract ScreenHandlerType<?> getType();
}
