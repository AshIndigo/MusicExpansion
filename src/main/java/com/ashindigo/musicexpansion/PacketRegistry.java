package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.entity.RecordMakerEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PacketRegistry {

    // Packet Identifiers
    public static final Identifier CHANGE_SLOT_PACKET = new Identifier(MusicExpansion.MODID, "change_slot");
    public static final Identifier CREATE_RECORD = new Identifier(MusicExpansion.MODID, "create_record");
    public static final Identifier ALL_RECORDS = new Identifier(MusicExpansion.MODID, "all_records");
    public static final Identifier PLAY_JUKEBOX_TRACK = new Identifier(MusicExpansion.MODID, "play_track");
    public static final Identifier SYNC_EVENTS = new Identifier(MusicExpansion.MODID, "sync_events");
    public static final Identifier ALL_PLAYERS_SERVER = new Identifier(MusicExpansion.MODID, "all_players_server");
    public static final Identifier ALL_PLAYERS_CLIENT = new Identifier(MusicExpansion.MODID, "all_players_client");
    public static final Identifier SET_VOLUME = new Identifier(MusicExpansion.MODID, "set_volume");

    public static void registerServerPackets() {
        // Change slot on disc holder
        ServerSidePacketRegistry.INSTANCE.register(CHANGE_SLOT_PACKET, (packetContext, attachedData) -> {
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
                        if (maker.getStack(1).isEmpty() && !maker.getStack(0).isEmpty() && maker.getStack(0).getItem() == MusicExpansion.blankRecord) {
                            maker.setStack(1, disc.copy());
                            maker.removeStack(0, 1);
                            maker.markDirty();
                        }
                    }
                }
            });
        });
        ServerSidePacketRegistry.INSTANCE.register(ALL_PLAYERS_SERVER, (packetContext, attachedData) -> {
            final MinecraftServer server = packetContext.getPlayer().getServer();
            String name = attachedData.readString();
            PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
            data.writeString(name);
            switch (name) {
                case "set_volume":
                case "stop_track":
                    data.writeItemStack(attachedData.readItemStack());
                    break;
                case "play_track":
                    data.writeItemStack(attachedData.readItemStack());
                    data.writeUuid(packetContext.getPlayer().getUuid());
                    break;
                default: return;
            }
            packetContext.getTaskQueue().execute(() -> {
                if (server != null) {
                    PlayerStream.all(server).forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ALL_PLAYERS_CLIENT, data));
                }
            });
        });
        ServerSidePacketRegistry.INSTANCE.register(SET_VOLUME, (packetContext, attachedData) -> {
            float volume = attachedData.readFloat();
            int invSlot = attachedData.readInt();
            packetContext.getTaskQueue().execute(() -> {
                if (packetContext.getPlayer().inventory.getStack(invSlot).hasTag()) {
                    packetContext.getPlayer().inventory.getStack(invSlot).getOrCreateTag().putFloat("volume", volume);
                    packetContext.getPlayer().inventory.markDirty();
                }
            });
        });
    }

}
