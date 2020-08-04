package com.ashindigo.musicexpansion.client;

import com.ashindigo.musicexpansion.MusicExpansion;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

// TODO Probably doesn't need to be its own class
public class CustomDiscModelPredicateProvider implements ModelPredicateProvider {
    @Override
    public float call(ItemStack stack, ClientWorld world, LivingEntity entity) {
        return MusicExpansion.tracks.indexOf(Identifier.tryParse(stack.getOrCreateTag().getString("track")));
    }
}
