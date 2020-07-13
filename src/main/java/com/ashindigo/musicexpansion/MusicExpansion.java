package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.container.WalkmanContainer;
import com.ashindigo.musicexpansion.item.ItemCustomRecord;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.ArrayList;

public class MusicExpansion implements ModInitializer {

    public static final String MODID = "musicexpansion";
    public static final Identifier PLAYDISK_PACKET = new Identifier(MODID, "playdisk");
    public static final Identifier STOPDISK_PACKET = new Identifier(MODID, "stopdisk");
    public static final Identifier CHANGESLOT_PACKET = new Identifier(MODID, "changeslot");
    public static ExtendedScreenHandlerType<WalkmanContainer> WALKMAN_TYPE;
    public static ItemWalkman walkman;

    @Override
    public void onInitialize() {
        walkman = new ItemWalkman();
        WALKMAN_TYPE = (ExtendedScreenHandlerType<WalkmanContainer>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "walkman"), (int syncId, PlayerInventory inv, PacketByteBuf buf) -> new WalkmanContainer(syncId, inv, buf.readInt()));
        Registry.register(Registry.ITEM, new Identifier(MODID, "walkman"), walkman);
        ArrayList<ItemCustomRecord> records = new ArrayList<>();
        try {
            records = RecordJsonParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ItemCustomRecord record : records) {
            Registry.register(Registry.ITEM, record.getID(), record);
//            AutoJsonApi.addEntry(record.getID(), new AutoConfig(AutoConfig.AutoConfigTextureMode.EXTERNAL, AutoConfig.AutoConfigType.ITEM).setLangName(new TranslatableText("item.minecraft.music_disc_13").asString()));
//            AutoJsonApi.addSoundEntry(record.getID(), record.getEvent()); // TODO Updating AutoJsonApi?
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
    }
}
