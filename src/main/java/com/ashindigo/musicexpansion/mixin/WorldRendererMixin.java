package com.ashindigo.musicexpansion.mixin;

import com.ashindigo.musicexpansion.accessor.WorldRendererAccessor;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements WorldRendererAccessor {

    @Shadow
    @Final
    private Map<BlockPos, SoundInstance> playingSongs;

    @Override
    public Map<BlockPos, SoundInstance> musicexpansion_getPlayingSongs() {
        return playingSongs;
    }
}
