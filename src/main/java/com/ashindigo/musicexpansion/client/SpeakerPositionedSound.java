package com.ashindigo.musicexpansion.client;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class SpeakerPositionedSound extends PositionedSoundInstance implements ControllableVolume, TickableSoundInstance {

    private final int id;
    private boolean done = false;

    public SpeakerPositionedSound(SoundEvent sound, float volume, BlockPos pos, int id) {
        super(sound, SoundCategory.RECORDS, volume, 1.0F, pos);
        this.id = id;
    }

    @Override
    public void setVolume(float vol) {
        // TODO
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void tick() {

    }
}
