//package com.ashindigo.musicexpansion.block;
//
//import com.ashindigo.musicexpansion.entity.HASControllerEntity;
//import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
//import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
//import net.minecraft.block.BlockWithEntity;
//import net.minecraft.block.Material;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.world.BlockView;
//
//public class HASControllerBlock extends BlockWithEntity {
//
//    public HASControllerBlock() {
//        super(FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.PICKAXES, 2));
//    }
//
//    @Override
//    public BlockEntity createBlockEntity(BlockView world) {
//        return new HASControllerEntity();
//    }
//}
