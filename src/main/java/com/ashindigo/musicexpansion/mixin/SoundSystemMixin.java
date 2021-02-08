package com.ashindigo.musicexpansion.mixin;

import com.ashindigo.musicexpansion.client.BoomboxMovingSound;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {

    @Unique
    private boolean doubleA;

    @ModifyVariable(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/sound/SoundSystem;getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F"), index = 6, name = "g")
    public float musicexpansion_play(float g) {
        if (doubleA) {
            doubleA = false;
            return g * 2;
        }
        return g;
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundInstance;getCategory()Lnet/minecraft/sound/SoundCategory;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void musicexpansion_play(SoundInstance soundInstance, CallbackInfo ci, WeightedSoundSet weightedSoundSet, Identifier identifier, Sound sound, float f, float g) {
        doubleA = soundInstance instanceof BoomboxMovingSound;
    }
}
