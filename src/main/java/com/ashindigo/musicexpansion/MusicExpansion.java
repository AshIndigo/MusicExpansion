package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.handler.DiscRackHandler;
import com.ashindigo.musicexpansion.helpers.DiscHelper;
import com.ashindigo.musicexpansion.item.CustomRecordItem;
import com.ashindigo.musicexpansion.block.DiscRackBlock;
import com.ashindigo.musicexpansion.block.RecordMakerBlock;
import com.ashindigo.musicexpansion.entity.DiscRackEntity;
import com.ashindigo.musicexpansion.entity.RecordMakerEntity;
import com.ashindigo.musicexpansion.handler.BoomboxHandler;
import com.ashindigo.musicexpansion.handler.RecordMakerHandler;
import com.ashindigo.musicexpansion.handler.WalkmanHandler;
import com.ashindigo.musicexpansion.item.BoomboxItem;
import com.ashindigo.musicexpansion.item.CustomDiscItem;
import com.ashindigo.musicexpansion.item.WalkmanItem;
import com.ashindigo.musicexpansion.recipe.UpdateRecordRecipe;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class MusicExpansion implements ModInitializer {

    public static final String MODID = "musicexpansion";
    public static final String MODID_EXTERNAL = MODID + "external";
    // Packet Identifiers
    public static final Identifier CHANGESLOT_PACKET = new Identifier(MODID, "changeslot");
    public static final Identifier CREATE_RECORD = new Identifier(MODID, "createrecord");
    public static final Identifier ALL_RECORDS = new Identifier(MODID, "all_records");
    public static final Identifier PLAY_JUKEBOX_TRACK = new Identifier(MODID, "play_track");
    public static final Identifier SYNC_EVENTS = new Identifier(MODID, "sync_events");
    public static final Identifier PLAY_TRACK_FOR_ALL_SERVER = new Identifier(MODID, "play_track_for_all_server");
    public static final Identifier PLAY_TRACK_FOR_ALL_CLIENT = new Identifier(MODID, "play_track_for_all_client");
    public static final Identifier STOP_TRACK_FOR_ALL_SERVER = new Identifier(MODID, "stop_track_for_all_server");
    public static final Identifier STOP_TRACK_FOR_ALL_CLIENT = new Identifier(MODID, "stop_track_for_all_client");
    public static final Logger logger = LogManager.getLogger(MODID);
    public static SpecialRecipeSerializer<UpdateRecordRecipe> UPDATE_DISC;
    public static ExtendedScreenHandlerType<WalkmanHandler> WALKMAN_TYPE;
    public static ExtendedScreenHandlerType<BoomboxHandler> BOOMBOX_TYPE;
    public static ExtendedScreenHandlerType<RecordMakerHandler> RECORD_MAKER_TYPE;
    public static ExtendedScreenHandlerType<DiscRackHandler> DISC_RACK_TYPE;
    // Items/Blocks
    public static Item blankRecord;
    public static WalkmanItem walkman;
    public static BoomboxItem boombox;
    public static CustomDiscItem customDisc;
    public static RecordMakerBlock recordMakerBlock;
    public static DiscRackBlock discRackBlock;
    public static BlockEntityType<RecordMakerEntity> recordMakerEntity;
    public static BlockEntityType<DiscRackEntity> discRackEntity;
    public static final ItemGroup MUSIC_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "main"), () -> new ItemStack(walkman));
    public static ArrayList<Identifier> tracks = new ArrayList<>();

    @Override
    public void onInitialize() {
        registerItemsBlocks();
        WALKMAN_TYPE = (ExtendedScreenHandlerType<WalkmanHandler>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "walkman"), (syncId1, inv1, buf) -> new WalkmanHandler(syncId1, inv1, buf.readInt()));
        BOOMBOX_TYPE = (ExtendedScreenHandlerType<BoomboxHandler>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "boombox"), (syncId1, inv1, buf) -> new BoomboxHandler(syncId1, inv1, buf.readInt()));
        // Record Maker
        RECORD_MAKER_TYPE = (ExtendedScreenHandlerType<RecordMakerHandler>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "recordmaker"), (syncId, inv, buf) -> new RecordMakerHandler(syncId, inv, buf.readBlockPos()));
        recordMakerEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "recordmaker"), BlockEntityType.Builder.create(RecordMakerEntity::new, recordMakerBlock).build(null));
        // Disc Rack
        DISC_RACK_TYPE = (ExtendedScreenHandlerType<DiscRackHandler>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "discrack"), (syncId, inv, buf) -> new DiscRackHandler(syncId, inv, buf.readBlockPos()));
        discRackEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "discrack"), BlockEntityType.Builder.create(DiscRackEntity::new, discRackBlock).build(null));
        UPDATE_DISC = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "update_disc"), new SpecialRecipeSerializer<>(UpdateRecordRecipe::new));
        registerServerPackets();
        registerTracks();
        //registerOldRecords();
    }

    private static void registerItemsBlocks() {
        walkman = Registry.register(Registry.ITEM, new Identifier(MODID, "walkman"), new WalkmanItem());
        boombox = Registry.register(Registry.ITEM, new Identifier(MODID, "boombox"), new BoomboxItem());
        blankRecord = Registry.register(Registry.ITEM, new Identifier(MODID, "blank_record"), new Item(new Item.Settings().group(MUSIC_GROUP)));
        customDisc = Registry.register(Registry.ITEM, new Identifier(MODID, "custom_disc"), new CustomDiscItem());
        recordMakerBlock = Registry.register(Registry.BLOCK, new Identifier(MODID, "recordmaker"), new RecordMakerBlock());
        Registry.register(Registry.ITEM, new Identifier(MODID, "recordmaker"), new BlockItem(recordMakerBlock, new Item.Settings().group(MUSIC_GROUP)));
        discRackBlock = Registry.register(Registry.BLOCK, new Identifier(MODID, "discrack"), new DiscRackBlock());
        Registry.register(Registry.ITEM, new Identifier(MODID, "discrack"), new BlockItem(discRackBlock, new Item.Settings().group(MUSIC_GROUP)));
    }

    public static void registerServerPackets() {
        // Change slot on disc holder
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
        // Put a record in the Record Maker's result slot
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
        // Play the specified track for all players, and supply the senders UUID
        ServerSidePacketRegistry.INSTANCE.register(PLAY_TRACK_FOR_ALL_SERVER, (packetContext, attachedData) -> {
            MinecraftServer server = packetContext.getPlayer().getServer();
            PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
            data.writeItemStack(attachedData.readItemStack());
            data.writeUuid(packetContext.getPlayer().getUuid());
            packetContext.getTaskQueue().execute(() -> {
                if (server != null) {
                    PlayerStream.all(server).forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, PLAY_TRACK_FOR_ALL_CLIENT, data));
                }
            });
        });
        // Stop the track for all players
        ServerSidePacketRegistry.INSTANCE.register(STOP_TRACK_FOR_ALL_SERVER, (packetContext, attachedData) -> {
            MinecraftServer server = packetContext.getPlayer().getServer();
            PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
            data.writeItemStack(attachedData.readItemStack());
            packetContext.getTaskQueue().execute(() -> {
                if (server != null) {
                    PlayerStream.all(server).forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, STOP_TRACK_FOR_ALL_CLIENT, data));
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
            Registry.register(Registry.ITEM, track, new CustomRecordItem(track, new SoundEvent(track)));
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
