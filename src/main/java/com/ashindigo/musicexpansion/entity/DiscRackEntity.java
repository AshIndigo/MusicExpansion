package com.ashindigo.musicexpansion.entity;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.handler.DiscRackHandler;
import com.ashindigo.musicexpansion.item.CustomDiscItem;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import spinnery.common.inventory.BaseInventory;
import spinnery.common.utility.InventoryUtilities;

public class DiscRackEntity extends BlockEntity implements Inventory, BlockEntityClientSerializable, ExtendedScreenHandlerFactory {

    final DefaultedList<ItemStack> stacks;

    public DiscRackEntity() {
        super(MusicExpansion.DISC_RACK_ENTITY_TYPE);
        stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public void clear() {
        stacks.clear();
        markDirty();
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        BaseInventory inv = InventoryUtilities.read(tag);
        for (int i = 0; i < inv.size(); i++) {
            setStack(i, inv.getStack(i));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        InventoryUtilities.write(this, tag);
        super.toTag(tag);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        super.fromTag(getCachedState(), tag);
        BaseInventory inv = InventoryUtilities.read(tag);
        for (int i = 0; i < inv.size(); i++) {
            setStack(i, inv.getStack(i));
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        InventoryUtilities.write(this, tag);
        super.toTag(tag);
        return tag;
    }

    @Override
    public boolean isEmpty() {
        return stacks.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack split = stacks.get(slot).split(amount);
        markDirty();
        return split;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack remove = stacks.remove(slot);
        markDirty();
        return remove;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stacks.set(slot, stack);
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
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
        return new TranslatableText("block.musicexpansion.discrack");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new DiscRackHandler(syncId, inv, getPos());
    }

}
