package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.helpers.DiscHelper;
import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public abstract class Abstract9DiscItem extends Item implements ExtendedScreenHandlerFactory {

    public Abstract9DiscItem() {
        super(new Item.Settings().maxCount(1).group(MusicExpansion.MUSIC_GROUP));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(getDescription());
        tooltip.add(new TranslatableText("desc.musicexpansion.activekeybinds").formatted(Formatting.GRAY));
        tooltip.add(new TranslatableText("text.musicexpansion.activestatus").append(WordUtils.capitalize(String.valueOf(DiscHolderHelper.isActive(stack)))).formatted(Formatting.GRAY));
        if (MinecraftClient.getInstance().player != null) {
            ItemStack disc = DiscHolderHelper.getDiscInSlot(stack, DiscHolderHelper.getSelectedSlot(stack));
            if (!disc.isEmpty()) {
                tooltip.add(new TranslatableText("text.musicexpansion.currenttrack").append(DiscHelper.getDesc(disc)).formatted(Formatting.GRAY));
            } else {
                tooltip.add(new TranslatableText("text.musicexpansion.currenttrack.nothing").formatted(Formatting.GRAY));
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    public abstract Text getDescription();

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(player.getMainHandStack().getItem().getClass().isAssignableFrom(getClass()) ? Hand.MAIN_HAND.ordinal() : Hand.OFF_HAND.ordinal());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        DiscHolderHelper.setupInitialTags(player.getStackInHand(hand));
        player.inventory.markDirty();
        if (!world.isClient()) {
            if (player.isSneaking()) {
                DiscHolderHelper.toggleActive(player.getStackInHand(hand));
                player.inventory.markDirty();
                return TypedActionResult.success(player.getStackInHand(hand));
            } else {
                player.openHandledScreen(this);
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getTranslationKey());
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        DiscHolderHelper.setupInitialTags(stack);
    }

}
