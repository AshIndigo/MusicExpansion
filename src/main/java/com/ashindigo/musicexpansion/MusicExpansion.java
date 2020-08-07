package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.block.RecordMakerBlock;
import com.ashindigo.musicexpansion.entity.RecordMakerEntity;
import com.ashindigo.musicexpansion.handler.RecordMakerHandler;
import com.ashindigo.musicexpansion.handler.WalkmanHandler;
import com.ashindigo.musicexpansion.item.CustomDiscItem;
import com.ashindigo.musicexpansion.item.ItemCustomRecord;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import com.ashindigo.musicexpansion.recipe.UpdateRecordRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.sound.SoundEvent;
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
    public static final Identifier ALL_RECORDS = new Identifier(MODID, "all_records");
    public static final Identifier PLAY_TRACK = new Identifier(MODID, "play_track");
    public static final Identifier SYNC_EVENTS = new Identifier(MODID, "sync_events");
    public static SpecialRecipeSerializer<UpdateRecordRecipe> UPDATE_DISC;
    public static ExtendedScreenHandlerType<WalkmanHandler> WALKMAN_TYPE;
    public static ExtendedScreenHandlerType<RecordMakerHandler> RECORDMAKER_TYPE;
    public static Item blankRecord;
    public static ItemWalkman walkman;
    public static CustomDiscItem customDisc;
    public static RecordMakerBlock recordMakerBlock;
    public static BlockEntityType<RecordMakerEntity> recordMakerEntity;
    public static final ItemGroup MUSIC_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "main"), () -> new ItemStack(walkman));
    public static ArrayList<Identifier> tracks = new ArrayList<>();
    //LogManager.getLogger(MODID);

    @Override
    public void onInitialize() {
        registerItemsBlocks();
        WALKMAN_TYPE = (ExtendedScreenHandlerType<WalkmanHandler>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "walkman"), (int syncId, PlayerInventory inv, PacketByteBuf buf) -> new WalkmanHandler(syncId, inv));
        RECORDMAKER_TYPE = (ExtendedScreenHandlerType<RecordMakerHandler>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "recordmaker"), (int syncId, PlayerInventory inv, PacketByteBuf buf) -> new RecordMakerHandler(syncId, inv, buf.readBlockPos()));
        recordMakerEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "recordmaker"), BlockEntityType.Builder.create(RecordMakerEntity::new, recordMakerBlock).build(null));
        UPDATE_DISC = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "update_disc"), new SpecialRecipeSerializer<>(UpdateRecordRecipe::new));
        registerServerPackets();
        registerTracks();
        //registerOldRecords();
    }

    private static void registerItemsBlocks() {
        walkman = Registry.register(Registry.ITEM, new Identifier(MODID, "walkman"), new ItemWalkman());
        blankRecord = Registry.register(Registry.ITEM, new Identifier(MODID, "blank_record"), new Item(new Item.Settings().group(MUSIC_GROUP)));
        customDisc = Registry.register(Registry.ITEM, new Identifier(MODID, "custom_disc"), new CustomDiscItem());
        recordMakerBlock = Registry.register(Registry.BLOCK, new Identifier(MODID, "recordmaker"), new RecordMakerBlock());
        Registry.register(Registry.ITEM, new Identifier(MODID, "recordmaker"), new BlockItem(recordMakerBlock, new Item.Settings().group(MUSIC_GROUP)));
    }

    public static void registerServerPackets() {
        ServerSidePacketRegistry.INSTANCE.register(CHANGESLOT_PACKET, (packetContext, attachedData) -> {
            int slot = attachedData.readInt();
            int invSlot = attachedData.readInt();
            packetContext.getTaskQueue().execute(() -> {
                if (packetContext.getPlayer().inventory.getStack(invSlot).hasTag()) {
                    packetContext.getPlayer().inventory.getStack(invSlot).getOrCreateTag().putInt("selected", slot);
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
                    if (maker != null) {
                        if (maker.getStack(1).isEmpty() && !maker.getStack(0).isEmpty() && maker.getStack(0).getItem() == blankRecord) {
                            maker.setStack(1, disc.copy());
                            maker.removeStack(0, 1);
                            maker.markDirty();
                        }
                    }
                }
            });
        });
    }

    private void registerTracks() {
        try {
            tracks = RecordJsonParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Identifier id : tracks) {
            Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
        }
    }


    @Deprecated
    private static void registerOldRecords() {
        for (Identifier track : tracks) {
            Registry.register(Registry.ITEM, track, new ItemCustomRecord(track, new SoundEvent(track)));
        }
    }


    public static ArrayList<ItemStack> getCraftableRecords(boolean all) {
        ArrayList<ItemStack> discs = new ArrayList<>();
        for (Identifier id : tracks) {
            discs.add(DiscHelper.createCustomDisc(id));
        }
        if (all) {
            for (Item disc : ItemTags.getContainer().getOrCreate(ItemTags.MUSIC_DISCS.getId()).values()) {
                discs.add(new ItemStack(disc));
            }
        }
        return discs;
    }

}
