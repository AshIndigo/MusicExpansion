package com.ashindigo.musicexpansion.entity;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.container.RecordMakerContainer;
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

public class RecordMakerEntity extends BlockEntity implements Inventory, BlockEntityClientSerializable, ExtendedScreenHandlerFactory {

    final DefaultedList<ItemStack> stacks;

    public RecordMakerEntity() {
        super(MusicExpansion.recordMakerEntity);
        this.stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public void clear() {
        markDirty();
        stacks.clear();
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
        markDirty();
        return stacks.get(slot).split(amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        markDirty();
        return stacks.remove(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        markDirty();
        stacks.set(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }


    @Override
    public boolean isValid(int slot, ItemStack stack) { // 0 = blank records, 1 = result records
        if (stack.getItem() == MusicExpansion.blankRecord && slot == 0) {
            return true;
        } else {
            return stack.getItem() instanceof MusicDiscItem && slot == 1 && getStack(1).isEmpty();
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(MusicExpansion.recordMakerBlock.getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new RecordMakerContainer(syncId, inv, pos);
    }
}
