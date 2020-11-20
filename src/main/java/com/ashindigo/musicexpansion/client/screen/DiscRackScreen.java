package com.ashindigo.musicexpansion.client.screen;

import com.ashindigo.musicexpansion.description.DiscRackDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class DiscRackScreen extends CottonInventoryScreen<DiscRackDescription> {

    public DiscRackScreen(DiscRackDescription description, PlayerInventory playerInventory, Text title) {
        super(description, playerInventory.player, title); // new TranslatableText("block.musicexpansion.diskrack")
    }
}
