package com.ashindigo.musicexpansion.client.screen;

import com.ashindigo.musicexpansion.description.BoomboxDescription;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class BoomboxScreen extends Abstract9DiscScreen<BoomboxDescription> {

    public BoomboxScreen(BoomboxDescription handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title);
    }
}
