package com.ashindigo.musicexpansion.screen;

import com.ashindigo.musicexpansion.handler.BoomboxHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class BoomboxScreen extends Abstract9DiscScreen<BoomboxHandler> {

    public BoomboxScreen(BoomboxHandler handler, PlayerInventory playerInv, Text title, Consumer<ItemStack> playButton) {
        super(handler, playerInv, title, playButton);
    }
}
