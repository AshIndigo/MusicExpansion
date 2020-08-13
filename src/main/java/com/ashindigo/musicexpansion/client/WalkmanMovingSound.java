package com.ashindigo.musicexpansion.client;

import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;

public class WalkmanMovingSound extends MovingSoundInstance {

    private final SoundEvent soundEvent;
    private final PlayerEntity player;
    private final UUID discUUID;

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
            volume = 0.0F;
            return;
        }
        if (!this.player.isAlive() || !DiscHolderHelper.discHolderContainsSound(soundEvent, player.inventory, discUUID)) {
            setDone();
        } else {
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
            this.volume = 0.0F + MathHelper.clamp(1, 0.0F, 0.5F) * 0.7F;
        }
    }
}
