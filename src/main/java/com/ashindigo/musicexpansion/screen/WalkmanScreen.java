package com.ashindigo.musicexpansion.screen;

import com.ashindigo.musicexpansion.handler.WalkmanHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class WalkmanScreen extends Abstract9DiscScreen<WalkmanHandler> {

    public WalkmanScreen(WalkmanHandler handler, PlayerInventory playerInv, Text title, Consumer<ItemStack> playButton) {
        super(handler, playerInv, title, playButton);
    }
}
