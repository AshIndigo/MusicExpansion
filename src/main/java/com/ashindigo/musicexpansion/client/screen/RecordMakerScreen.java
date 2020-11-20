package com.ashindigo.musicexpansion.client.screen;

import com.ashindigo.musicexpansion.description.RecordMakerDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class RecordMakerScreen extends CottonInventoryScreen<RecordMakerDescription> {
    public RecordMakerScreen(RecordMakerDescription description, PlayerInventory playerInventory, Text title) {
        super(description, playerInventory.player, title); // new TranslatableText("block.musicexpansion.record_maker")
    }
}
