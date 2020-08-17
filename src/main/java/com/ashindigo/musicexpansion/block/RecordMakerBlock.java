package com.ashindigo.musicexpansion.block;

import com.ashindigo.musicexpansion.PacketRegistry;
import com.ashindigo.musicexpansion.RecordJsonParser;
import com.ashindigo.musicexpansion.entity.RecordMakerEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class RecordMakerBlock extends BlockWithEntity {

    public RecordMakerBlock() {
        super(FabricBlockSettings.of(Material.METAL).breakByHand(false).strength(3, 5).requiresTool().breakByTool(FabricToolTags.PICKAXES, 2));
    }

    @Override
    public void buildTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(new TranslatableText("desc.musicexpansion.recordmaker").formatted(Formatting.GRAY));
        super.buildTooltip(stack, world, tooltip, options);
    }

    @SuppressWarnings("deprecation") // onUse is deprecated for whatever reason.
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
            passedData.writeBoolean(RecordJsonParser.isAllRecords());
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PacketRegistry.ALL_RECORDS, passedData);
            player.openHandledScreen((ExtendedScreenHandlerFactory) world.getBlockEntity(pos));
        }
        return ActionResult.PASS;
    }

    @SuppressWarnings("deprecation") // onStateReplaced is deprecated for whatever reason
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RecordMakerEntity) {
                ItemScatterer.spawn(world, pos, (RecordMakerEntity) blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new RecordMakerEntity();
    }
}
