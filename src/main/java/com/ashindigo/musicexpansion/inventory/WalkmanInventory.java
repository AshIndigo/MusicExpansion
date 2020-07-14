package com.ashindigo.musicexpansion.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;

public class WalkmanInventory extends SimpleInventory {

    public WalkmanInventory() {
        super(9);
    } // TODO Functionally useless?

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.getItem() instanceof MusicDiscItem;
    }
}
