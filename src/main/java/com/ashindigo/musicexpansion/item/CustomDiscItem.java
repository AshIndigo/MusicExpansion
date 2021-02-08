package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.PacketRegistry;
import com.ashindigo.musicexpansion.helpers.DiscHelper;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

public class CustomDiscItem extends Item {

    public CustomDiscItem() {
        super(new Settings().group(MusicExpansion.MUSIC_GROUP).maxCount(1).rarity(Rarity.RARE));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (Registry.SOUND_EVENT.containsId(DiscHelper.getTrackID(stack))) {
            tooltip.add(new TranslatableText("item." + DiscHelper.getTrackID(stack).toString().replace(":", ".") + ".desc").formatted(Formatting.GRAY));
        } else {
            tooltip.add(new TranslatableText("text.musicexpansion.missing_event"));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    // Sets the item name to Minecraft's "Music Disc"
    @Override
    public String getTranslationKey() {
        return "item.minecraft.music_disc_far";
    }

    // Jukebox compat
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.JUKEBOX) && !blockState.get(JukeboxBlock.HAS_RECORD)) {
            ItemStack itemStack = context.getStack();
            if (!world.isClient) {
                ((JukeboxBlock) Blocks.JUKEBOX).setRecord(world, blockPos, blockState, itemStack);
                Collection<ServerPlayerEntity> watchingPlayers = PlayerLookup.around((ServerWorld) world, blockPos, 64);
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeItemStack(itemStack);
                passedData.writeBlockPos(blockPos);
                watchingPlayers.forEach(player -> ServerPlayNetworking.send(player, PacketRegistry.PLAY_JUKEBOX_TRACK, passedData));
                itemStack.decrement(1);
                PlayerEntity playerEntity = context.getPlayer();
                if (playerEntity != null) {
                    playerEntity.incrementStat(Stats.PLAY_RECORD);
                }
            }

            return ActionResult.success(world.isClient);
        } else {
            return ActionResult.PASS;
        }
    }

}
