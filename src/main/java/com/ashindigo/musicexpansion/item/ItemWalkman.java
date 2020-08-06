package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.DiscHelper;
import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.MusicHelper;
import com.ashindigo.musicexpansion.handler.WalkmanHandler;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class ItemWalkman extends Item implements ExtendedScreenHandlerFactory {

    public ItemWalkman() {
        super(new Item.Settings().maxCount(1).group(MusicExpansion.MUSIC_GROUP));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("desc.musicexpansion.walkman").formatted(Formatting.GRAY));
        tooltip.add(new TranslatableText("desc.musicexpansion.onlyonewalkman").formatted(Formatting.GRAY));
        if (MinecraftClient.getInstance().player != null) {
            ItemStack disc = MusicHelper.getDiscInSlot(stack, ItemWalkman.getSelectedSlot(stack));
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
        if (getWalkmansInInv(player.inventory) == 1) {
            getSelectedSlot(player.getStackInHand(hand)); // Hack
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

//    public static WalkmanInventory getInventory(ItemStack stack, PlayerInventory inv) { // TODO Maybe clean up?
//        if (!inv.player.world.isClient || !stack.getTag().contains("Items")) {
//            if (!stack.hasTag() || !stack.getTag().contains("Items")) {
//                stack.setTag(Inventories.toTag(stack.getTag(), new WalkmanInventory().getStacks()));
//                inv.markDirty();
//            }
//        }
//        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(9, ItemStack.EMPTY);
//        Inventories.fromTag(stack.getTag(), stacks);
//        return new WalkmanInventory(stacks);
//    }

    public static WalkmanInventory getInventory(ItemStack stack, PlayerInventory inv) {
        if (!stack.getOrCreateTag().contains("Items")) {
            if (!inv.player.world.isClient) { // Set up inventory tag if needed, and copy over the selected slot int
                int slot = getSelectedSlot(stack);
                stack.setTag(Inventories.toTag(stack.getTag(), new WalkmanInventory().getStacks()));
                CompoundTag tag = stack.getOrCreateTag();
                tag.putInt("selected", slot);
                stack.setTag(tag);
                inv.markDirty();
            }
        }
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(9, ItemStack.EMPTY);
        Inventories.fromTag(stack.getOrCreateTag(), stacks);
        return new WalkmanInventory(stacks);
    }

    public static int getSelectedSlot(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("selected")) {
            return tag.getInt("selected");
        } else {
            tag.putInt("selected", 0);
            stack.setTag(tag);
            return 0;
        }
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
        return new WalkmanHandler(syncId, inv);
    }
}
