package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.DiscHelper;
import com.ashindigo.musicexpansion.MusicExpansion;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class CustomDiscItem extends Item {

    public CustomDiscItem() {
        super(new Settings().group(MusicExpansion.MUSIC_GROUP).maxCount(1).rarity(Rarity.RARE));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (Registry.SOUND_EVENT.containsId(DiscHelper.getTrackID(stack))) {
            tooltip.add(new TranslatableText("item." + DiscHelper.getTrackID(stack).toString().replace(":", ".") + ".desc").formatted(Formatting.GRAY));
        } else {
            tooltip.add(new TranslatableText("text.musicexpansion.missingevent"));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    // Sets the item name to Minecraft's "Music Disc"
    @Override
    public String getTranslationKey() {
        return "item.minecraft.music_disc_far";
    }

}
