package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.MusicExpansion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ItemCustomRecord extends MusicDiscItem {

    private final SoundEvent event;
    private final Identifier id;

    public ItemCustomRecord(Identifier id, SoundEvent event) {
        super(15, event, new Settings().group(MusicExpansion.MUSIC_GROUP).maxCount(1).rarity(Rarity.RARE)); // Sorry redstoners
        this.event = event;
        this.id = id;
    }

    public SoundEvent getEvent() {
        return event;
    }

    public Identifier getId() {
        return id;
    }

    // Make sure description stays the same, based on the one in the file
    @Override
    @Environment(EnvType.CLIENT)
    public MutableText getDescription() {
        return new TranslatableText("item." + getId().toString().replace(":", ".") + ".desc").formatted(Formatting.GRAY);
    }

    // Sets the item name to Minecraft's "Music Disc"
    @Override
    public String getTranslationKey() {
        return "item.minecraft.music_disc_far";
    }
}
