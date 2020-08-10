package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.handler.WalkmanHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

public class WalkmanItem extends Abstract9DiscItem {

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new WalkmanHandler(syncId, inv, player.getMainHandStack().getItem().getClass().isAssignableFrom(getClass()) ? Hand.MAIN_HAND.ordinal() : Hand.OFF_HAND.ordinal());
    }

    @Override
    public Text getDescription() {
        return new TranslatableText("desc.musicexpansion.walkman").formatted(Formatting.GRAY);
    }
}
