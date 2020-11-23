package com.ashindigo.musicexpansion.entity;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.description.DiscRackDescription;
import com.ashindigo.musicexpansion.item.CustomDiscItem;
import com.ashindigo.musicexpansion.misc.BasicSidedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class DiscRackEntity extends BlockEntity implements BasicSidedInventory, ExtendedScreenHandlerFactory, InventoryProvider {

    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public DiscRackEntity() {
        super(MusicExpansion.DISC_RACK_ENTITY_TYPE);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.getItem() instanceof MusicDiscItem || stack.getItem() instanceof CustomDiscItem;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(getPos());
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(MusicExpansion.discRackBlock.getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new DiscRackDescription(syncId, inv, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return this;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, stacks);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, stacks);
        return super.toTag(tag);
    }
}
