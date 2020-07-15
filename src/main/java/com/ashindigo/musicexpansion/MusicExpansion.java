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
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.ArrayList;

public class MusicExpansion implements ModInitializer {

    public static final String MODID = "musicexpansion";
    public static final String MODID_EXTERNAL = MODID + "external";
    public static final Identifier CHANGESLOT_PACKET = new Identifier(MODID, "changeslot");
    public static ExtendedScreenHandlerType<WalkmanContainer> WALKMAN_TYPE;
    public static ItemWalkman walkman;
    public static ArrayList<ItemCustomRecord> records = new ArrayList<>();

    // TODO Needed things for custom records
    // Record recipes or Machine that makes the records

    @Override
    public void onInitialize() {
        WALKMAN_TYPE = (ExtendedScreenHandlerType<WalkmanContainer>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "walkman"), (int syncId, PlayerInventory inv, PacketByteBuf buf) -> new WalkmanContainer(syncId, inv));
        walkman = Registry.register(Registry.ITEM, new Identifier(MODID, "walkman"), new ItemWalkman());
        try {
            records = RecordJsonParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ItemCustomRecord record : records) {
            Registry.register(Registry.ITEM, record.getID(), record);
            Registry.register(Registry.SOUND_EVENT, record.getID(), record.getEvent());
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
