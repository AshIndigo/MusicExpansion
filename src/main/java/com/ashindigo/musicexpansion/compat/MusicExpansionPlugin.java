package com.ashindigo.musicexpansion.compat;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.helpers.DiscHelper;
import com.google.common.collect.Lists;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class MusicExpansionPlugin implements REIPluginV0 {
    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(MusicExpansion.MODID, MusicExpansion.MODID + "_plugin");
    }

    @Override
    public void registerEntries(EntryRegistry entryRegistry) {
        List<EntryStack> customDiscs = Lists.newArrayList();
        for (Identifier id : MusicExpansion.tracks) {
            ItemStack stack = new ItemStack(MusicExpansion.customDisc);
            DiscHelper.setTrack(stack, id);
            customDiscs.add(EntryStack.create(stack));
        }
        entryRegistry.registerEntriesAfter(EntryStack.create(new ItemStack(MusicExpansion.customDisc)), customDiscs);
    }
}
