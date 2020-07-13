package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.WalkmanInventory;
import com.ashindigo.musicexpansion.container.WalkmanContainer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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
    // TOOD
    // Needs Tooltip

    public ItemWalkman() {
        super(new Item.Settings().maxCount(1).group(ItemGroup.MISC));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        getSelectedSlot(player.getStackInHand(hand)); // Hack
        player.inventory.markDirty();
        if (player.isSneaking()) {
            if (!world.isClient()) {
                player.openHandledScreen(new ExtendedScreenHandlerFactory() { // TODO Does it need to be extended?
                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                        //buf.writeItemStack(player.getStackInHand(hand));
                        buf.writeInt(player.inventory.getSlotWithStack(player.getStackInHand(hand)));
                    }

                    @Override
                    public Text getDisplayName() {
                        return new TranslatableText(getTranslationKey());
                    }

                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return new WalkmanContainer(syncId, inv, player.inventory.getSlotWithStack(player.getStackInHand(hand)));
                    }
                });
            }
//        } else {
//            if (!getInventory(player.getStackInHand(hand), player.inventory).isEmpty()) { // TODO Check
//                if (!world.isClient) {
//                    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
//                    passedData.writeItemStack(getInventory(player.getStackInHand(hand), player.inventory).getStack(0));
//                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, MusicExpansion.PLAYDISK_PACKET, passedData);
//
//                }
//            } else {
//                if (!world.isClient) {
//                    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer()); // No need?
//                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, MusicExpansion.STOPDISK_PACKET, passedData);
//                }
//            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new WalkmanContainer(syncId, inv, 0); // Useless?
    }

    public static BaseInventory getInventory(ItemStack stack, PlayerInventory inv) {
        if (!stack.hasTag() || !stack.getTag().contains("inventory")) {
            stack.setTag(InventoryUtilities.write(new WalkmanInventory()));
            inv.markDirty();
        }
        return InventoryUtilities.read(stack.getTag());
    }

    public static int getSelectedSlot(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains("selected")) {
            stack.getOrCreateTag().putInt("selected", 0);
        }
        return stack.getTag().getInt("selected");
    }

    public static void setSelectedSlot(ItemStack stack, int slot, int invSlot) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(slot);
        buf.writeInt(invSlot);
        ClientSidePacketRegistry.INSTANCE.sendToServer(MusicExpansion.CHANGESLOT_PACKET, buf);
    }

}
