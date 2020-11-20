package com.ashindigo.musicexpansion.client.screen;

import com.ashindigo.musicexpansion.description.WalkmanDescription;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class WalkmanScreen extends Abstract9DiscScreen<WalkmanDescription> {

    public WalkmanScreen(WalkmanDescription description, PlayerInventory playerInventory, Text title) {
        super(description, playerInventory, title);
    }
}
