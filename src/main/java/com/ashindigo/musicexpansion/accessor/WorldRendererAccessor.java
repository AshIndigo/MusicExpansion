package com.ashindigo.musicexpansion.accessor;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public interface WorldRendererAccessor {

    Map<BlockPos, SoundInstance> musicexpansion_getPlayingSongs();
}
