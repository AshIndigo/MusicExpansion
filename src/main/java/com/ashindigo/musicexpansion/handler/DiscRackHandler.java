//package com.ashindigo.musicexpansion.handler;
//
//import com.ashindigo.musicexpansion.MusicExpansion;
//import com.ashindigo.musicexpansion.entity.DiscRackEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.screen.ScreenHandlerType;
//import net.minecraft.util.math.BlockPos;
//import spinnery.common.handler.BaseScreenHandler;
//import spinnery.widget.WInterface;
//import spinnery.widget.WSlot;
//
//public class DiscRackHandler extends BaseScreenHandler {
//
//    public final DiscRackEntity discRack;
//    public static final int INVENTORY = 1;
//
//    public DiscRackHandler(int synchronizationID, PlayerInventory playerInventory, BlockPos pos) {
//        super(synchronizationID, playerInventory);
//        discRack = (DiscRackEntity) world.getBlockEntity(pos);
//        addInventory(INVENTORY, discRack);
//        WInterface mainInterface = getInterface();
//        WSlot.addHeadlessPlayerInventory(mainInterface);
//        WSlot.addHeadlessArray(mainInterface, 0, INVENTORY, 9, 1);
//    }
//
//    @Override
//    public ScreenHandlerType<?> getType() {
//        return MusicExpansion.DISC_RACK_HANDLER_TYPE;
//    }
//}
