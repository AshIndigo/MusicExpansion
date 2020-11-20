//package com.ashindigo.musicexpansion.block;
//
//import com.ashindigo.musicexpansion.entity.SpeakerEntity;
//import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
//import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
//import net.minecraft.block.BlockWithEntity;
//import net.minecraft.block.Material;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.world.BlockView;
//
//public class SpeakerBlock extends BlockWithEntity {
//    public SpeakerBlock() {
//        super(FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.PICKAXES, 2));
//    }
//
//    @Override
//    public BlockEntity createBlockEntity(BlockView world) {
//        return new SpeakerEntity();
//    }
//}
