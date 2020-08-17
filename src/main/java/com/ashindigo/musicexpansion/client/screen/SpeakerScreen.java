package com.ashindigo.musicexpansion.client.screen;

import com.ashindigo.musicexpansion.handler.SpeakerHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import spinnery.client.screen.BaseHandledScreen;

public class SpeakerScreen extends BaseHandledScreen<SpeakerHandler> {

    public SpeakerScreen(SpeakerHandler handler, PlayerInventory playerInv, Text title) {
        super(title, handler, playerInv.player);
    }
}
