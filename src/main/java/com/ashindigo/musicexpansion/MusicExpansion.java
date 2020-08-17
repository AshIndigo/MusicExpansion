package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.block.DiscRackBlock;
import com.ashindigo.musicexpansion.block.HASControllerBlock;
import com.ashindigo.musicexpansion.block.RecordMakerBlock;
import com.ashindigo.musicexpansion.block.SpeakerBlock;
import com.ashindigo.musicexpansion.entity.DiscRackEntity;
import com.ashindigo.musicexpansion.entity.HASControllerEntity;
import com.ashindigo.musicexpansion.entity.RecordMakerEntity;
import com.ashindigo.musicexpansion.entity.SpeakerEntity;
import com.ashindigo.musicexpansion.handler.*;
import com.ashindigo.musicexpansion.helpers.DiscHelper;
import com.ashindigo.musicexpansion.item.BoomboxItem;
import com.ashindigo.musicexpansion.item.CustomDiscItem;
import com.ashindigo.musicexpansion.item.CustomRecordItem;
import com.ashindigo.musicexpansion.item.WalkmanItem;
import com.ashindigo.musicexpansion.recipe.UpdateRecordRecipe;
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
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spinnery.widget.api.Size;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Supplier;

// TODO Future Ash notes
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
    public static final Size SLOT_SIZE = Size.of(18, 18);
    public static SpecialRecipeSerializer<UpdateRecordRecipe> UPDATE_DISC;
    public static ExtendedScreenHandlerType<WalkmanHandler> WALKMAN_HANDLER_TYPE;
    public static ExtendedScreenHandlerType<BoomboxHandler> BOOMBOX_HANDLER_TYPE;
    public static ExtendedScreenHandlerType<RecordMakerHandler> RECORD_MAKER_HANDLER_TYPE;
    public static ExtendedScreenHandlerType<DiscRackHandler> DISC_RACK_HANDLER_TYPE;
    public static ExtendedScreenHandlerType<HASControllerHandler> HAS_CONTROLLER_HANDLER_TYPE;
    public static ExtendedScreenHandlerType<SpeakerHandler> SPEAKER_HANDLER_TYPE;
    // Items/Blocks
    public static Item blankRecord;
    public static WalkmanItem walkman;
    public static BoomboxItem boombox;
    public static CustomDiscItem customDisc;
    public static RecordMakerBlock recordMakerBlock;
    public static DiscRackBlock discRackBlock;
    public static HASControllerBlock hasControllerBlock;
    public static SpeakerBlock speakerBlock;
    public static BlockEntityType<RecordMakerEntity> RECORD_MAKER_ENTITY_TYPE;
    public static BlockEntityType<DiscRackEntity> DISC_RACK_ENTITY_TYPE;
    public static BlockEntityType<HASControllerEntity> HAS_CONTROLLER_ENTITY_TYPE;
    public static BlockEntityType<SpeakerEntity> SPEAKER_ENTITY_TYPE;
    public static final ItemGroup MUSIC_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "main"), () -> new ItemStack(walkman));
    public static ArrayList<Identifier> tracks = new ArrayList<>();

    @Override
    public void onInitialize() {
        registerItemsBlocks();
        // Whole bunch of handler/entity registry stuff
        WALKMAN_HANDLER_TYPE = registerHandler("walkman", (integer, inventory, packetByteBuf) -> new WalkmanHandler(integer, inventory, packetByteBuf.readInt()));
        BOOMBOX_HANDLER_TYPE = registerHandler("boombox", (integer, inventory, packetByteBuf) -> new BoomboxHandler(integer, inventory, packetByteBuf.readInt()));
        RECORD_MAKER_HANDLER_TYPE = registerHandler("recordmaker", (integer, inventory, packetByteBuf) -> new RecordMakerHandler(integer, inventory, packetByteBuf.readBlockPos()));
        RECORD_MAKER_ENTITY_TYPE = registerBlockEntity("recordmaker", RecordMakerEntity::new, recordMakerBlock);
        DISC_RACK_HANDLER_TYPE = registerHandler("disc_rack", (integer, inventory, packetByteBuf) -> new DiscRackHandler(integer, inventory, packetByteBuf.readBlockPos()));
        DISC_RACK_ENTITY_TYPE = registerBlockEntity("disc_rack", DiscRackEntity::new, discRackBlock);
        HAS_CONTROLLER_HANDLER_TYPE = registerHandler("has_controller", (integer, inventory, packetByteBuf) -> new HASControllerHandler(integer, inventory, packetByteBuf.readBlockPos()));
        HAS_CONTROLLER_ENTITY_TYPE = registerBlockEntity("has_controller", HASControllerEntity::new, hasControllerBlock);
        SPEAKER_HANDLER_TYPE = registerHandler("speaker", (integer, inventory, packetByteBuf) -> new SpeakerHandler(integer, inventory, packetByteBuf.readBlockPos()));
        SPEAKER_ENTITY_TYPE = registerBlockEntity("speaker", SpeakerEntity::new, speakerBlock);
        // Upgrade old disc recipe
        UPDATE_DISC = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "update_disc"), new SpecialRecipeSerializer<>(UpdateRecordRecipe::new));
        PacketRegistry.registerServerPackets();
        registerTracks();
        //registerOldRecords();
    }

    private static void registerItemsBlocks() {
        walkman = registerItem("walkman", WalkmanItem::new);
        boombox = registerItem("boombox", BoomboxItem::new);
        blankRecord = registerItem("blank_record", () -> new Item(new Item.Settings().group(MUSIC_GROUP)));
        customDisc = registerItem("custom_disc", CustomDiscItem::new);
        recordMakerBlock = registerBlock("recordmaker", RecordMakerBlock::new);
        discRackBlock = registerBlock("disc_rack", DiscRackBlock::new);
        hasControllerBlock = registerBlock("has_controller", HASControllerBlock::new);
        speakerBlock = registerBlock("speaker", SpeakerBlock::new);

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
