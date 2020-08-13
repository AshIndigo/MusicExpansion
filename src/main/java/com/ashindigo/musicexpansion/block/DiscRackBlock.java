package com.ashindigo.musicexpansion.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class DiscRackBlock extends BlockWithEntity {

    protected DiscRackBlock() {
        super(FabricBlockSettings.of(Material.WOOD).breakByHand(true));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return null;
    }
}
