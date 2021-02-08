package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.block.DiscRackBlock;
import com.ashindigo.musicexpansion.block.RecordMakerBlock;
import com.ashindigo.musicexpansion.description.BoomboxDescription;
import com.ashindigo.musicexpansion.description.DiscRackDescription;
import com.ashindigo.musicexpansion.description.RecordMakerDescription;
import com.ashindigo.musicexpansion.description.WalkmanDescription;
import com.ashindigo.musicexpansion.entity.DiscRackEntity;
import com.ashindigo.musicexpansion.entity.RecordMakerEntity;
import com.ashindigo.musicexpansion.helpers.DiscHelper;
import com.ashindigo.musicexpansion.item.BoomboxItem;
import com.ashindigo.musicexpansion.item.CustomDiscItem;
import com.ashindigo.musicexpansion.item.WalkmanItem;
import com.mojang.datafixers.util.Function3;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Supplier;

// TODO Future Ash notes
// Custom tracks need resource pack to be reloaded
// TODO HAS needs to be finished/reimplemented
// TODO Minecraft's audio sucks
// HAS Stuff:
// GUI needs a field to input the number
// Need to add speakers too to actually act as a source of sound.
// Is chunkloading gonna be an issue?
// Speaker:
// Gotta add recipes/models/loot table
public class MusicExpansion implements ModInitializer {

    public static final String MODID = "musicexpansion";
    public static final String MODID_EXTERNAL = MODID + "external";
    public static final Logger logger = LogManager.getLogger(MODID);
    public static ExtendedScreenHandlerType<WalkmanDescription> WALKMAN_HANDLER_TYPE;
    public static ExtendedScreenHandlerType<BoomboxDescription> BOOMBOX_HANDLER_TYPE;
    public static ExtendedScreenHandlerType<RecordMakerDescription> RECORD_MAKER_HANDLER_TYPE;
    public static ExtendedScreenHandlerType<DiscRackDescription> DISC_RACK_HANDLER_TYPE;
    //public static ExtendedScreenHandlerType<HASControllerHandler> HAS_CONTROLLER_HANDLER_TYPE;
    //public static ExtendedScreenHandlerType<SpeakerHandler> SPEAKER_HANDLER_TYPE;
    // Items/Blocks
    public static Item blankRecord;
    public static WalkmanItem walkman;
    public static final ItemGroup MUSIC_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "main"), () -> new ItemStack(walkman));
    public static BoomboxItem boombox;
    public static CustomDiscItem customDisc;
    public static RecordMakerBlock recordMakerBlock;
    public static DiscRackBlock discRackBlock;
//    public static HASControllerBlock hasControllerBlock;
//    public static SpeakerBlock speakerBlock;
    public static BlockEntityType<RecordMakerEntity> RECORD_MAKER_ENTITY_TYPE;
    public static BlockEntityType<DiscRackEntity> DISC_RACK_ENTITY_TYPE;
//    public static BlockEntityType<HASControllerEntity> HAS_CONTROLLER_ENTITY_TYPE;
//    public static BlockEntityType<SpeakerEntity> SPEAKER_ENTITY_TYPE;
    public static ArrayList<Identifier> tracks = new ArrayList<>();
    public static ArrayList<Identifier> tracksOrig;

    private static void registerItemsBlocks() {
        walkman = registerItem("walkman", WalkmanItem::new);
        boombox = registerItem("boombox", BoomboxItem::new);
        blankRecord = registerItem("blank_record", () -> new Item(new Item.Settings().group(MUSIC_GROUP)));
        customDisc = registerItem("custom_disc", CustomDiscItem::new);
        recordMakerBlock = registerBlock("record_maker", RecordMakerBlock::new);
        discRackBlock = registerBlock("disc_rack", DiscRackBlock::new);
//        hasControllerBlock = registerBlock("has_controller", HASControllerBlock::new);
//        speakerBlock = registerBlock("speaker", SpeakerBlock::new);

    }

    private static <T extends Item> T registerItem(String name, Supplier<T> item) {
        return Registry.register(Registry.ITEM, new Identifier(MODID, name), item.get());
    }

    private static <T extends Block> T registerBlock(String name, Supplier<T> block) {
        Identifier id = new Identifier(MODID, name);
        T var = Registry.register(Registry.BLOCK, id, block.get());
        Registry.register(Registry.ITEM, id, new BlockItem(var, new Item.Settings().group(MUSIC_GROUP)));
        return var;
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, Supplier<T> supplier, Block block) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, name), BlockEntityType.Builder.create(supplier, block).build(null));
    }

    private static <T extends ScreenHandler> ExtendedScreenHandlerType<T> registerHandler(String name, Function3<Integer, PlayerInventory, PacketByteBuf, T> func) {
        return (ExtendedScreenHandlerType<T>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, name), func::apply);
    }

    public static ArrayList<ItemStack> getCraftableRecords(boolean all) {
        ArrayList<ItemStack> discs = new ArrayList<>();
        for (Identifier id : tracks) {
            discs.add(DiscHelper.createCustomDisc(id));
        }
        if (all) {
            for (Item disc : ItemTags.getTagGroup().getTagOrEmpty(ItemTags.MUSIC_DISCS.getId()).values()) {
                discs.add(new ItemStack(disc));
            }
        }
        return discs;
    }

    @Override
    public void onInitialize() {
        registerItemsBlocks();
        // Whole bunch of handler/entity registry stuff
        WALKMAN_HANDLER_TYPE = registerHandler("walkman", (integer, inventory, packetByteBuf) -> new WalkmanDescription(integer, inventory, packetByteBuf.readInt()));
        BOOMBOX_HANDLER_TYPE = registerHandler("boombox", (integer, inventory, packetByteBuf) -> new BoomboxDescription(integer, inventory, packetByteBuf.readInt()));
        RECORD_MAKER_HANDLER_TYPE = registerHandler("record_maker", (syncId, playerInventory, packetByteBuf) -> new RecordMakerDescription(syncId, playerInventory, ScreenHandlerContext.create(playerInventory.player.getEntityWorld(), packetByteBuf.readBlockPos())));
        RECORD_MAKER_ENTITY_TYPE = registerBlockEntity("record_maker", RecordMakerEntity::new, recordMakerBlock);
        DISC_RACK_HANDLER_TYPE = registerHandler("disc_rack", (syncId, playerInventory, packetByteBuf) -> new DiscRackDescription(syncId, playerInventory, ScreenHandlerContext.create(playerInventory.player.getEntityWorld(), packetByteBuf.readBlockPos())));
        DISC_RACK_ENTITY_TYPE = registerBlockEntity("disc_rack", DiscRackEntity::new, discRackBlock);
//        HAS_CONTROLLER_HANDLER_TYPE = registerHandler("has_controller", (integer, inventory, packetByteBuf) -> new HASControllerHandler(integer, inventory, packetByteBuf.readBlockPos()));
//        HAS_CONTROLLER_ENTITY_TYPE = registerBlockEntity("has_controller", HASControllerEntity::new, hasControllerBlock);
//        SPEAKER_HANDLER_TYPE = registerHandler("speaker", (integer, inventory, packetByteBuf) -> new SpeakerHandler(integer, inventory, packetByteBuf.readBlockPos()));
//        SPEAKER_ENTITY_TYPE = registerBlockEntity("speaker", SpeakerEntity::new, speakerBlock);
        PacketRegistry.registerServerPackets();
        registerTracks();
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
        tracksOrig = new ArrayList<>(tracks);
    }
}
