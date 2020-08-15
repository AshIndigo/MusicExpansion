package com.ashindigo.musicexpansion.client;

import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import java.util.UUID;

public class WalkmanMovingSound extends MovingSoundInstance implements ControllableVolume {

    private final SoundEvent soundEvent;
    private final PlayerEntity player;
    private final UUID discUUID;
    private float backupVolume;

    public WalkmanMovingSound(SoundEvent soundEvent, UUID discUUID) {
        super(soundEvent, SoundCategory.RECORDS);
        this.soundEvent = soundEvent;
        this.player = MinecraftClient.getInstance().player;
        this.discUUID = discUUID;
        this.repeat = false;
        this.repeatDelay = 0;
    }

    @Override
    public void tick() { // https://stackoverflow.com/questions/57277755/music-discs-have-do-not-get-quieter-by-distance-in-my-minecraft-1-14-4-mod
        if (!DiscHolderHelper.containsUUID(discUUID, player.inventory)) {
            if (volume > 0.0F) {
                backupVolume = volume;
            }
            volume = 0.0F;
            return;
        }
        if (!this.player.isAlive() || !DiscHolderHelper.discHolderContainsSound(soundEvent, player.inventory, discUUID)) {
            setDone();
        } else {
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
            this.volume = backupVolume;
        }
    }

    @Override
    public void setVolume(float vol) {
        volume = vol;
        backupVolume = vol;
    }
}
