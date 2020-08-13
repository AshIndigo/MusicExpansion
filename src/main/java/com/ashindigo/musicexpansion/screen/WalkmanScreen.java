package com.ashindigo.musicexpansion.screen;

import com.ashindigo.musicexpansion.handler.WalkmanHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class WalkmanScreen extends Abstract9DiscScreen<WalkmanHandler> {

    public WalkmanScreen(WalkmanHandler handler, PlayerInventory playerInv, Text title) {
        super(handler, playerInv, title);
    }
}
