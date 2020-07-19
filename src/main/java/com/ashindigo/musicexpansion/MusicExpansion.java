package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.block.RecordMakerBlock;
import com.ashindigo.musicexpansion.container.RecordMakerContainer;
import com.ashindigo.musicexpansion.container.WalkmanContainer;
import com.ashindigo.musicexpansion.entity.RecordMakerEntity;
import com.ashindigo.musicexpansion.item.ItemCustomRecord;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.ArrayList;

public class MusicExpansion implements ModInitializer {

    public static final String MODID = "musicexpansion";
    public static final String MODID_EXTERNAL = MODID + "external";
    public static final Identifier CHANGESLOT_PACKET = new Identifier(MODID, "changeslot");
    public static final Identifier CREATE_RECORD = new Identifier(MODID, "createrecord");
    public static ExtendedScreenHandlerType<WalkmanContainer> WALKMAN_TYPE;
    public static ExtendedScreenHandlerType<RecordMakerContainer> RECORDMAKER_TYPE;
    public static Item blankRecord;
    public static ItemWalkman walkman;
    public static ArrayList<ItemCustomRecord> records = new ArrayList<>();
    public static BlockEntityType<RecordMakerEntity> recordMakerEntity;
    public static final ItemGroup MUSIC_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "main"), () -> new ItemStack(walkman));

    public static ArrayList<? extends MusicDiscItem> getCraftableRecords() {
        ArrayList<MusicDiscItem> discs = new ArrayList<>(records);
        if (RecordJsonParser.isAllRecords()) {
            for (Item disc : ItemTags.getContainer().getOrCreate(ItemTags.MUSIC_DISCS.getId()).values()) {
                discs.add((MusicDiscItem) disc);
            }
        }
        return discs;
    }

    @Override
    public void onInitialize() {
        WALKMAN_TYPE = (ExtendedScreenHandlerType<WalkmanContainer>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "walkman"), (int syncId, PlayerInventory inv, PacketByteBuf buf) -> new WalkmanContainer(syncId, inv));
        RECORDMAKER_TYPE = (ExtendedScreenHandlerType<RecordMakerContainer>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "recordmaker"), (int syncId, PlayerInventory inv, PacketByteBuf buf) -> new RecordMakerContainer(syncId, inv, buf.readBlockPos()));
        walkman = Registry.register(Registry.ITEM, new Identifier(MODID, "walkman"), new ItemWalkman());
        blankRecord = Registry.register(Registry.ITEM, new Identifier(MODID, "blank_record"), new Item(new Item.Settings().group(MUSIC_GROUP)));
        RecordMakerBlock recordMaker = Registry.register(Registry.BLOCK, new Identifier(MODID, "recordmaker"), new RecordMakerBlock());
        Registry.register(Registry.ITEM, new Identifier(MODID, "recordmaker"), new BlockItem(recordMaker, new Item.Settings().group(MUSIC_GROUP)));
        recordMakerEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "recordmaker"), BlockEntityType.Builder.create(RecordMakerEntity::new, recordMaker).build(null));
        try {
            records = RecordJsonParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ItemCustomRecord record : records) {
            Registry.register(Registry.ITEM, record.getId(), record);
            Registry.register(Registry.SOUND_EVENT, record.getId(), record.getEvent());
        }
        ServerSidePacketRegistry.INSTANCE.register(CHANGESLOT_PACKET, (packetContext, attachedData) -> {
            int slot = attachedData.readInt();
            int invSlot = attachedData.readInt();
            packetContext.getTaskQueue().execute(() -> {
                if (packetContext.getPlayer().inventory.getStack(invSlot).hasTag()) {
                    packetContext.getPlayer().inventory.getStack(invSlot).getTag().putInt("selected", slot);
                    packetContext.getPlayer().inventory.markDirty();
                }
            });
        });
        ServerSidePacketRegistry.INSTANCE.register(CREATE_RECORD, (packetContext, attachedData) -> {
            BlockPos pos = attachedData.readBlockPos();
            ItemStack disc = attachedData.readItemStack();
            packetContext.getTaskQueue().execute(() -> {
                if (packetContext.getPlayer().world.getBlockEntity(pos) != null && packetContext.getPlayer().world.getBlockEntity(pos) instanceof RecordMakerEntity) {
                    RecordMakerEntity maker = (RecordMakerEntity) packetContext.getPlayer().world.getBlockEntity(pos);
                    if (maker.getStack(1).isEmpty() && !maker.getStack(0).isEmpty() && maker.getStack(0).getItem() == blankRecord) {
                        maker.setStack(1, disc.copy());
                        maker.removeStack(0, 1);
                        maker.markDirty();
                    }
                }
            });
        });
    }

    public static int getWalkman(PlayerInventory inventory) {
        int slot = -1;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() instanceof ItemWalkman) {
                slot = i;
            }
        }
        return slot;
    }

}
