package com.ashindigo.musicexpansion.handler;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.handler.Abstract9DiscHolderHandler;
import com.ashindigo.musicexpansion.item.BoomboxItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;

public class BoomboxHandler extends Abstract9DiscHolderHandler {

    public BoomboxHandler(int syncId, PlayerInventory inv, int hand) {
        super(syncId, inv, hand, BoomboxItem.class);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return MusicExpansion.BOOMBOX_TYPE;
    }
}
