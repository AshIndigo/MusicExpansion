package com.ashindigo.musicexpansion.widget;

import io.github.cottonmc.cotton.gui.widget.WButton;
import net.minecraft.item.ItemStack;

public class WRecordButton extends WButton {

    private ItemStack record = ItemStack.EMPTY;

    public void setRecord(ItemStack record) {
        this.record = record;
    }

    public ItemStack getRecord() {
        return record.copy();
    }
}
