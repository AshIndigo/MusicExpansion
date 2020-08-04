package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.DiscHelper;
import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.MusicHelper;
import com.ashindigo.musicexpansion.container.WalkmanContainer;
import com.ashindigo.musicexpansion.inventory.WalkmanInventory;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class ItemWalkman extends Item implements ScreenHandlerFactory {

    public ItemWalkman() {
        super(new Item.Settings().maxCount(1).group(MusicExpansion.MUSIC_GROUP));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("desc.musicexpansion.walkman").formatted(Formatting.GRAY));
        tooltip.add(new TranslatableText("desc.musicexpansion.onlyonewalkman").formatted(Formatting.GRAY));
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
    public boolean isNetworkSynced() { // TODO Use
        return super.isNetworkSynced();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (getWalkmansInInv(player.inventory) == 1) {
            getSelectedSlot(player.getStackInHand(hand)); // Hack
            player.inventory.markDirty();
            if (!world.isClient()) {
                player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                        buf.writeInt(DiscHelper.getWalkman(player.inventory));
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
        } else {
            if (world.isClient)
            player.sendMessage(new TranslatableText("text.musicexpansion.onlyonewalkman.try"), false);
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new WalkmanContainer(syncId, inv); // Useless?
    }

    public static WalkmanInventory getInventory(ItemStack stack, PlayerInventory inv) { // TODO Maybe clean up?
        if (!inv.player.world.isClient || !stack.getTag().contains("Items")) {
            if (!stack.hasTag() || !stack.getTag().contains("Items")) {
                stack.setTag(Inventories.toTag(stack.getTag(), new WalkmanInventory().getStacks()));
                inv.markDirty();
            }
        }
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(9, ItemStack.EMPTY);
        Inventories.fromTag(stack.getTag(), stacks);
        return new WalkmanInventory(stacks);
    }

    public static int getSelectedSlot(ItemStack stack) { // TODO Maybe make better?
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

    public static int getWalkmansInInv(PlayerInventory inventory) {
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() instanceof ItemWalkman) {
                count++;
            }
        }
        return count;
    }
}
