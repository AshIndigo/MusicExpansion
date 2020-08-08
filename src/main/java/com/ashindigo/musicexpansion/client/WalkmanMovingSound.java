package com.ashindigo.musicexpansion.client;

import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import com.ashindigo.musicexpansion.item.WalkmanItem;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

public class WalkmanMovingSound extends MovingSoundInstance {

    private final SoundEvent soundEvent;
    private final PlayerEntity player;

    public WalkmanMovingSound(SoundEvent soundEvent, PlayerEntity player) {
        super(soundEvent, SoundCategory.RECORDS);
        this.soundEvent = soundEvent;
        this.player = player;
        this.repeat = false;
        this.repeatDelay = 0;
    }

    @Override
    public void tick() { // https://stackoverflow.com/questions/57277755/music-discs-have-do-not-get-quieter-by-distance-in-my-minecraft-1-14-4-mod
        if (DiscHolderHelper.getDiscHoldersInInv(WalkmanItem.class, player.inventory) == 0) {
            volume = 0.0F;
            return;
        }
        if (!this.player.isAlive() || !DiscHolderHelper.discHolderContainsSound(WalkmanItem.class, soundEvent, player.inventory)) {
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
