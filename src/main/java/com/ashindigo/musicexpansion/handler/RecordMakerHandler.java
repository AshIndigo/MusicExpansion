package com.ashindigo.musicexpansion.handler;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.RecordJsonParser;
import com.ashindigo.musicexpansion.entity.RecordMakerEntity;
import com.ashindigo.musicexpansion.widget.WTooltipDisc;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import spinnery.common.handler.BaseScreenHandler;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;

public class RecordMakerHandler extends BaseScreenHandler {

    public final RecordMakerEntity recordMaker;
    public static final int INVENTORY = 1;

    public RecordMakerHandler(int synchronizationID, PlayerInventory playerInventory, BlockPos pos) {
        super(synchronizationID, playerInventory);
        recordMaker = ((RecordMakerEntity) getWorld().getBlockEntity(pos));
        addInventory(INVENTORY, recordMaker);
        WInterface mainInterface = getInterface();
        WSlot.addHeadlessPlayerInventory(mainInterface);
        mainInterface.createChild(WSlot::new).setSlotNumber(0).setInventoryNumber(INVENTORY).accept(MusicExpansion.blankRecord).setWhitelist(); // Empty disc slot
        mainInterface.createChild(WSlot::new).setSlotNumber(1).setInventoryNumber(INVENTORY);//.accept(MusicExpansion.recordsOld.toArray(new ItemCustomRecord[]{})).setWhitelist();// Result Slot
        for (ItemStack stack : MusicExpansion.getCraftableRecords(RecordJsonParser.isAllRecords())) {
            mainInterface.createChild(WTooltipDisc::new).setStack(stack);
        }
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return MusicExpansion.RECORDMAKER_TYPE;
    }
}
