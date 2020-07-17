package com.ashindigo.musicexpansion.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalkmanInventory implements Inventory, InventoryChangedListener {

    final DefaultedList<ItemStack> stacks;
    final List<InventoryChangedListener> listeners = new ArrayList<>();

    public WalkmanInventory() {
        this.stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);
    }

    public WalkmanInventory(DefaultedList<ItemStack> stacks) {
        this.stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            this.stacks.set(i, stack);
        }
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public void clear() {
        markDirty();
        stacks.clear();
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
    public void markDirty() {
        onInventoryChanged(this);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.getItem() instanceof MusicDiscItem;
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        listeners.forEach(inventoryListener -> inventoryListener.onInventoryChanged(sender));
    }

    public void addListener(InventoryChangedListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    @SuppressWarnings("unused") // Keeping for potential use later
    public void removeListener(InventoryChangedListener... listeners) {
        this.listeners.removeAll(Arrays.asList(listeners));
    }

    public DefaultedList<ItemStack> getStacks() {
        return stacks;
    }
}
