package com.ashindigo.musicexpansion;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;

public class WalkmanInventory extends SimpleInventory {

    public WalkmanInventory() {
        super(9);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.getItem() instanceof MusicDiscItem;
    }
}
