//package com.ashindigo.musicexpansion.handler;
//
//import com.ashindigo.musicexpansion.MusicExpansion;
//import com.ashindigo.musicexpansion.entity.HASControllerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.screen.ScreenHandlerType;
//import net.minecraft.util.math.BlockPos;
//import spinnery.common.handler.BaseScreenHandler;
//import spinnery.widget.WInterface;
//import spinnery.widget.WSlot;
//
//public class HASControllerHandler extends BaseScreenHandler {
//
//    public final HASControllerEntity controller;
//    public static final int INVENTORY = 1;
//
//    public HASControllerHandler(int synchronizationID, PlayerInventory playerInventory, BlockPos pos) {
//        super(synchronizationID, playerInventory);
//        controller = (HASControllerEntity) world.getBlockEntity(pos);
//        addInventory(INVENTORY, controller);
//        WInterface mainInterface = getInterface();
//        WSlot.addHeadlessPlayerInventory(mainInterface);
//        WSlot.addHeadlessArray(mainInterface, 0, INVENTORY, 9, 1);
//    }
//
//    @Override
//    public ScreenHandlerType<?> getType() {
//        return MusicExpansion.HAS_CONTROLLER_HANDLER_TYPE;
//    }
//}
