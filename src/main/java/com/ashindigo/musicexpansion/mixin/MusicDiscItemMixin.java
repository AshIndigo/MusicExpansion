package com.ashindigo.musicexpansion.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MusicDiscItem.class)
public class MusicDiscItemMixin extends Item implements MusicDiscItemAccessor {

    @Shadow
    @Final
    private SoundEvent sound;

    public MusicDiscItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public SoundEvent musicexpansion_getSound() {
        return sound;
    }
}
