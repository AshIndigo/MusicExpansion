package com.ashindigo.musicexpansion.container;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import spinnery.common.handler.BaseScreenHandler;
import spinnery.common.inventory.BaseInventory;
import spinnery.common.utility.InventoryUtilities;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class WalkmanContainer extends BaseScreenHandler {

    public static final int INVENTORY = 1;
    public final int slot;

    public WalkmanContainer(int syncId, PlayerInventory inv, int slot) {
        super(syncId, inv); // MusicExpansion.WALKMAN_TYPE
        this.slot = slot;
        WInterface mainInterface = getInterface();
        BaseInventory walkmanInv = ItemWalkman.getInventory(inv.getStack(slot), inv);
        addInventory(INVENTORY, walkmanInv);
        walkmanInv.addListener(new InventoryChangedListener() {
            @Override
            public void onInventoryChanged(Inventory sender) {
                inv.getStack(slot).getTag().put("inventory", InventoryUtilities.write(sender).getCompound("inventory"));
                inv.markDirty();
            }
        });
        for (int i = 0; i < 9; i++) {
            mainInterface.createChild(WSlot::new).setSlotNumber(i).setInventoryNumber(INVENTORY);
        }
        //mainInterface.createChild(WSlot::new).setSlotNumber(0).setInventoryNumber(INVENTORY);
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
