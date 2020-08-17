package com.ashindigo.musicexpansion.entity;

import com.ashindigo.musicexpansion.MusicExpansion;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

public class SpeakerEntity extends BlockEntity implements BlockEntityClientSerializable {

    public SpeakerEntity() {
        super(MusicExpansion.SPEAKER_ENTITY_TYPE);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {

    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return null;
    }
}
