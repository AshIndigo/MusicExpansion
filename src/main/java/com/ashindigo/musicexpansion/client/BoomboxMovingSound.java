package com.ashindigo.musicexpansion.client;

import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;

public class BoomboxMovingSound extends MovingSoundInstance {

    private final SoundEvent soundEvent;
    private final PlayerEntity player;
    private final UUID uuid;
    private final UUID hostUUID;

    public BoomboxMovingSound(SoundEvent soundEvent, UUID uuidDisc, UUID hostUUID) {
        super(soundEvent, SoundCategory.RECORDS);
        this.soundEvent = soundEvent;
        this.player = MinecraftClient.getInstance().player;
        this.uuid = uuidDisc;
        this.hostUUID = hostUUID;
        this.repeat = false;
        this.repeatDelay = 0;
    }

    @Override
    public void tick() { // https://stackoverflow.com/questions/57277755/music-discs-have-do-not-get-quieter-by-distance-in-my-minecraft-1-14-4-mod
        if (MinecraftClient.getInstance().world != null) {
            PlayerEntity host = MinecraftClient.getInstance().world.getPlayerByUuid(hostUUID);
            if (host != null) {
                if (!DiscHolderHelper.containsUUID(uuid, host.inventory)) { // If the host does not have the boombox in their inventory then shut off volume
                    volume = 0.0F;
                    return;
                }
                if (!player.isAlive() || !host.isAlive() || !DiscHolderHelper.discHolderContainsSound(soundEvent, host.inventory, uuid)) { // If the listener is dead or the host no longer has the disc in their boombox, then stop
                    setDone();
                } else {
                    this.x = (float) host.getX();
                    this.y = (float) host.getY();
                    this.z = (float) host.getZ();
                    this.volume = 0.0F + MathHelper.clamp(1, 0.0F, 0.5F) * 0.7F;
                }
            } else {
                setDone();
            }
        }
    }
}
