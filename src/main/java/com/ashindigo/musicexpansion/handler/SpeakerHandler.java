package com.ashindigo.musicexpansion.handler;

import com.ashindigo.musicexpansion.entity.SpeakerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import spinnery.common.handler.BaseScreenHandler;

public class SpeakerHandler extends BaseScreenHandler {

    public SpeakerEntity speaker;

    public SpeakerHandler(int synchronizationID, PlayerInventory playerInventory, BlockPos pos) {
        super(synchronizationID, playerInventory);
        speaker = (SpeakerEntity) world.getBlockEntity(pos);
    }
}
