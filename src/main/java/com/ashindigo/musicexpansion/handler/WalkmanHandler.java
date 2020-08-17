package com.ashindigo.musicexpansion.handler;

import com.ashindigo.musicexpansion.MusicExpansion;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;

public class WalkmanHandler extends Abstract9DiscHolderHandler {

    public WalkmanHandler(int syncId, PlayerInventory inv, int hand) {
        super(syncId, inv, hand);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return MusicExpansion.WALKMAN_HANDLER_TYPE;
    }
}
