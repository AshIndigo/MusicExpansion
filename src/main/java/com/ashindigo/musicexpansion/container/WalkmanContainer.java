package com.ashindigo.musicexpansion.container;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.inventory.WalkmanInventory;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.screen.ScreenHandlerType;
import spinnery.common.handler.BaseScreenHandler;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;

public class WalkmanContainer extends BaseScreenHandler {

    public static final int INVENTORY = 1;

    public WalkmanContainer(int syncId, PlayerInventory inv) {
        super(syncId, inv);
        WInterface mainInterface = getInterface();
        int slot = MusicExpansion.getWalkman(inv);
        WalkmanInventory walkmanInv = ItemWalkman.getInventory(inv.getStack(slot), inv);
        addInventory(INVENTORY, walkmanInv);
        walkmanInv.addListener(sender -> {
            if (!inv.player.world.isClient) {
                // Set the walkman tag in inventory, by getting the tag and setting the "Items" tag to the resulting ListTag from Inventories.toTag() using the stacks from the current inventory
                inv.getStack(slot).getOrCreateTag().put("Items", Inventories.toTag(inv.getStack(slot).getTag(), walkmanInv.getStacks()).getList("Items", 10));
                inv.markDirty();
            }
        });
        for (int i = 0; i < 9; i++) {
            mainInterface.createChild(WSlot::new).setSlotNumber(i).setInventoryNumber(INVENTORY);
        }
        WSlot.addHeadlessPlayerInventory(mainInterface);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return MusicExpansion.WALKMAN_TYPE;
    }
}
