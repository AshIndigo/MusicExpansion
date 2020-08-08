package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.handler.WalkmanHandler;
import com.ashindigo.musicexpansion.helpers.DiscHelper;
import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class WalkmanItem extends Abstract9DiscItem implements ExtendedScreenHandlerFactory {

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("desc.musicexpansion.walkman").formatted(Formatting.GRAY));
        tooltip.add(new TranslatableText("desc.musicexpansion.onlyonewalkman").formatted(Formatting.GRAY));
        if (MinecraftClient.getInstance().player != null) {
            ItemStack disc = DiscHolderHelper.getDiscInSlot(stack, DiscHolderHelper.getSelectedSlot(stack));
            if (!disc.isEmpty()) {
                tooltip.add(new TranslatableText("text.musicexpansion.currenttrack").append(DiscHelper.getDesc(disc)));
            } else {
                tooltip.add(new TranslatableText("text.musicexpansion.currenttrack.nothing"));
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (DiscHolderHelper.getDiscHoldersInInv(WalkmanItem.class, player.inventory) == 1) {
            DiscHolderHelper.getSelectedSlot(player.getStackInHand(hand)); // Hack
            player.inventory.markDirty();
            if (!world.isClient()) {
                player.openHandledScreen(this);
            }
        } else {
            if (world.isClient)
                player.sendMessage(new TranslatableText("text.musicexpansion.onlyonewalkman.try"), false);
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new WalkmanHandler(syncId, inv);
    }
}
