package com.ashindigo.musicexpansion;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

public class WalkmanMovingSound extends MovingSoundInstance {

    private final PlayerEntity player;

    public WalkmanMovingSound(SoundEvent soundEvent, PlayerEntity player) {
        super(soundEvent, SoundCategory.RECORDS);
        this.player = player;
        this.repeat = false;
        this.repeatDelay = 0;
    }

    @Override
    public void tick() {
        if (!this.player.isAlive()) {
            setDone();
        } else {
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
            float f = 1.0f;
            this.volume = 0.0F + MathHelper.clamp(f, 0.0F, 0.5F) * 0.7F;
        }
    }
}
