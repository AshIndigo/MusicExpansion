package com.ashindigo.musicexpansion.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {

   @Accessor
   Map<BlockPos, SoundInstance> getPlayingSongs();
}
