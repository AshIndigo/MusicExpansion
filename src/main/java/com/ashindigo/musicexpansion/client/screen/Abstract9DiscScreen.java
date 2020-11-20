package com.ashindigo.musicexpansion.client.screen;

import com.ashindigo.musicexpansion.description.Abstract9DiscHolderDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class Abstract9DiscScreen<B extends Abstract9DiscHolderDescription> extends CottonInventoryScreen<B> {
    public Abstract9DiscScreen(B description, PlayerInventory playerInventory, Text title) {
        super(description, playerInventory.player, title);
    }
}
