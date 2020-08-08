package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.MusicExpansion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;

@Deprecated
public class CustomRecordItem extends MusicDiscItem {

    private final Identifier id;

    public CustomRecordItem(Identifier id, SoundEvent event) {
        super(15, event, new Settings().group(MusicExpansion.MUSIC_GROUP).maxCount(1).rarity(Rarity.RARE)); // Sorry redstoners
        this.id = id;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText("text.musicexpansion.olddisc"));
    }

    // Make sure description stays the same, based on the one in the file
    @Override
    @Environment(EnvType.CLIENT)
    public MutableText getDescription() {
        return new TranslatableText("item." + id.toString().replace(":", ".") + ".desc").formatted(Formatting.GRAY);
    }

    // Sets the item name to Minecraft's "Music Disc"
    @Override
    public String getTranslationKey() {
        return "item.minecraft.music_disc_far";
    }
}
