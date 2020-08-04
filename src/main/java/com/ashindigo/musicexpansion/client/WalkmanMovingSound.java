package com.ashindigo.musicexpansion.client;

import com.ashindigo.musicexpansion.DiscHelper;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;

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
        if (ItemWalkman.getWalkmansInInv(player.inventory) == 0) {
            volume = 0.0F;
            return;
        }
        //ItemWalkman.getInventory(player.inventory.getStack(DiscHelper.getWalkman(player.inventory)), player.inventory).containsAny(Collections.singleton(currentDisc))
        if (!this.player.isAlive() || !DiscHelper.walkmanContainsSound(soundEvent, player.inventory)) {
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
