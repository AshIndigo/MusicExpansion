package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.block.DiscRackBlock;
import com.ashindigo.musicexpansion.entity.DiscRackEntity;
import com.ashindigo.musicexpansion.helpers.DiscHelper;
import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import com.ashindigo.musicexpansion.inventory.Generic9DiscInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public abstract class Abstract9DiscItem extends Item implements ExtendedScreenHandlerFactory {

    public Abstract9DiscItem() {
        super(new Item.Settings().maxCount(1).group(MusicExpansion.MUSIC_GROUP));
    }

    @Override
    @Environment(EnvType.CLIENT)
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

    @Environment(EnvType.CLIENT)
    public abstract void playSelectedDisc(ItemStack stack);

    @Environment(EnvType.CLIENT)
    public abstract void stopSelectedDisc(ItemStack stack);

    public abstract void setVolume(ItemStack stack, float volume);

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(player.getMainHandStack().getItem().getClass().isAssignableFrom(getClass()) ? Hand.MAIN_HAND.ordinal() : Hand.OFF_HAND.ordinal());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        DiscHolderHelper.setupInitialTags(player.getStackInHand(hand));
        player.inventory.markDirty();
        if (!world.isClient()) {
            if (!isRightClickingOnRack(world, player)) {
                if (player.isSneaking()) {
                    DiscHolderHelper.toggleActive(player.getStackInHand(hand));
                    player.inventory.markDirty();
                    return TypedActionResult.success(player.getStackInHand(hand));
                } else {
                    player.openHandledScreen(this);
                }
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    // Hard coded reach distance that was copied from ClientPlayerInteractionManager#getReachDistance()
    // Could pose an issue later?
    private boolean isRightClickingOnRack(World world, PlayerEntity player) {
        HitResult result = player.rayTrace(player.isCreative() ? 5 : 4.5, 1, false);
        if (result.getType() == HitResult.Type.BLOCK) {
            return world.getBlockState(new BlockPos(result.getPos())).getBlock() instanceof DiscRackBlock;
        }
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        final int size = 9;
        if (!context.getWorld().isClient) {
            BlockEntity be = context.getWorld().getBlockEntity(context.getBlockPos());
            if (be instanceof DiscRackEntity) {
                DiscRackEntity discRack = (DiscRackEntity) be;
                ItemStack stack = context.getStack();
                PlayerEntity player = context.getPlayer();
                if (player != null && player.isSneaking()) {
                    Generic9DiscInventory discInv = DiscHolderHelper.getInventory(stack, context.getPlayer().inventory);
                    DefaultedList<ItemStack> oldDiscs = DefaultedList.ofSize(size, ItemStack.EMPTY);
                    for (int i = 0; i < size; i++) { // Get discs in disc holder to backup
                        oldDiscs.set(i, discInv.getStack(i).copy()); // Make backup
                        discInv.setStack(i, discRack.getStack(i).copy()); // And change to discs from rack
                        discRack.setStack(i, oldDiscs.get(i).copy());
                    }
                    CompoundTag invTag = toTag(stack.getTag(), discInv.getStacks()); // Set the new inventory
                    if (invTag != null) {
                        stack.getOrCreateTag().put("Items", invTag.getList("Items", 10));
                        player.inventory.markDirty();
                    }
                    discRack.markDirty();
                    discRack.sync();
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
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

    public CompoundTag toTag(CompoundTag tag, DefaultedList<ItemStack> stacks) {
        ListTag listTag = new ListTag();

        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack itemStack = stacks.get(i);
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte) i);
            itemStack.toTag(compoundTag);
            listTag.add(compoundTag);
        }

        if (!listTag.isEmpty()) {
            tag.put("Items", listTag);
        }

        return tag;
    }

}
