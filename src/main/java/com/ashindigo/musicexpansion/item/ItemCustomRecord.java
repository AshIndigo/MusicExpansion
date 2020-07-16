package com.ashindigo.musicexpansion.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ItemCustomRecord extends MusicDiscItem {

    private final SoundEvent event;
    private final Identifier id;

    public ItemCustomRecord(Identifier id, SoundEvent event) {
        super(15, event, new Settings().group(ItemGroup.MISC).maxCount(1)); // Sorry redstoners
        this.event = event;
        this.id = id;
    }

    public SoundEvent getEvent() {
        return event;
    }

    public Identifier getId() {
        return id;
    }
}
