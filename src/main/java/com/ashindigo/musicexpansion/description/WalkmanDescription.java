package com.ashindigo.musicexpansion.description;

import com.ashindigo.musicexpansion.MusicExpansion;
import net.minecraft.entity.player.PlayerInventory;

public class WalkmanDescription extends Abstract9DiscHolderDescription {

    public WalkmanDescription(int syncId, PlayerInventory inv, int hand) {
        super(MusicExpansion.WALKMAN_HANDLER_TYPE, syncId, inv, hand);
    }
}
