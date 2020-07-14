package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.MusicHelper;
import com.ashindigo.musicexpansion.WalkmanInventory;
import com.ashindigo.musicexpansion.container.WalkmanContainer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import spinnery.common.inventory.BaseInventory;
import spinnery.common.utility.InventoryUtilities;

import java.util.List;

public class ItemWalkman extends Item implements ScreenHandlerFactory {

    public ItemWalkman() {
        super(new Item.Settings().maxCount(1).group(ItemGroup.MISC));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (MinecraftClient.getInstance().player != null) {
            MusicDiscItem disc = MusicHelper.getDiscInSlot(stack, ItemWalkman.getSelectedSlot(stack));
            if (disc != null) {
                tooltip.add(new TranslatableText("text.musicexpansion.currenttrack").append(disc.getDescription()));
            } else {
                tooltip.add(new TranslatableText("text.musicexpansion.currenttrack.nothing"));
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        getSelectedSlot(player.getStackInHand(hand)); // Hack
        player.inventory.markDirty();
        if (player.isSneaking()) {
            if (!world.isClient()) {
                player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                        buf.writeInt(MusicExpansion.getWalkman(player.inventory));
                    }

                    @Override
                    public Text getDisplayName() {
                        return new TranslatableText(getTranslationKey());
                    }

                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return new WalkmanContainer(syncId, inv);
                    }
                });
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new WalkmanContainer(syncId, inv); // Useless?
    }

    public static BaseInventory getInventory(ItemStack stack, PlayerInventory inv) {
        if (!inv.player.world.isClient || !stack.getTag().contains("inventory")) {
            if (!stack.hasTag() || !stack.getTag().contains("inventory")) {
                stack.setTag(InventoryUtilities.write(new WalkmanInventory()));
                inv.markDirty();
            }
        }
        return InventoryUtilities.read(stack.getTag());
    }

    public static int getSelectedSlot(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains("selected")) {
            stack.getOrCreateTag().putInt("selected", 0);
        }
        return stack.getTag().getInt("selected");
    }

    public static void setSelectedSlot(int slot, int invSlot) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(slot);
        buf.writeInt(invSlot);
        ClientSidePacketRegistry.INSTANCE.sendToServer(MusicExpansion.CHANGESLOT_PACKET, buf);
    }

}
