package com.ashindigo.musicexpansion.description;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.description.Abstract9DiscHolderDescription;
import net.minecraft.entity.player.PlayerInventory;

public class BoomboxDescription extends Abstract9DiscHolderDescription {

    public BoomboxDescription(int syncId, PlayerInventory inv, int hand) {
        super(MusicExpansion.BOOMBOX_HANDLER_TYPE, syncId, inv, hand);
    }
}
