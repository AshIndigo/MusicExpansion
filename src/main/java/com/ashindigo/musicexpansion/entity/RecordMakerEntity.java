package com.ashindigo.musicexpansion.entity;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.description.RecordMakerDescription;
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

public class RecordMakerEntity extends BlockEntity implements BasicSidedInventory, ExtendedScreenHandlerFactory, InventoryProvider {

    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public RecordMakerEntity() {
        super(MusicExpansion.RECORD_MAKER_ENTITY_TYPE);
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

    @Override
    public boolean isValid(int slot, ItemStack stack) { // 0 = blank records, 1 = result records
        if (stack.getItem() == MusicExpansion.blankRecord && slot == 0) {
            return true;
        } else {
            return (stack.getItem() instanceof MusicDiscItem || stack.getItem() instanceof CustomDiscItem) && slot == 1 && getStack(1).isEmpty();
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
        return new RecordMakerDescription(syncId, inv, ScreenHandlerContext.create(player.getEntityWorld(), getPos()));
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return this;
    }
}
