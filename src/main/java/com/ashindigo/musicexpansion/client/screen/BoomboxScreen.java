package com.ashindigo.musicexpansion.client.screen;

import com.ashindigo.musicexpansion.handler.BoomboxHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class BoomboxScreen extends Abstract9DiscScreen<BoomboxHandler> {

    public BoomboxScreen(BoomboxHandler handler, PlayerInventory playerInv, Text title) {
        super(handler, playerInv, title);
    }
}
