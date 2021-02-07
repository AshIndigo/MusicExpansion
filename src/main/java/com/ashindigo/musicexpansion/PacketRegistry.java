package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.entity.RecordMakerEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
        ServerPlayNetworking.registerGlobalReceiver(CHANGE_SLOT_PACKET, (server, player, handler, buf, sender) -> {
            int slot = buf.readInt();
            int invSlot = buf.readInt();
            server.execute(() -> {
                if (player.inventory.getStack(invSlot).hasTag()) {
                    player.inventory.getStack(invSlot).getOrCreateTag().putInt("selected", slot);
                    player.inventory.markDirty();
                }
            });
        });
        // Put a record in the Record Maker's result slot
        ServerPlayNetworking.registerGlobalReceiver(CREATE_RECORD, (server, player, handler, buf, sender) -> {
            BlockPos pos = buf.readBlockPos();
            ItemStack disc = buf.readItemStack();
            server.execute(() -> {
                if (player.world.getBlockEntity(pos) != null && player.world.getBlockEntity(pos) instanceof RecordMakerEntity) {
                    RecordMakerEntity maker = (RecordMakerEntity) player.world.getBlockEntity(pos);
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
        ServerPlayNetworking.registerGlobalReceiver(ALL_PLAYERS_SERVER, (server, player, handler, buf, sender) -> {
            String name = buf.readString();
            PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
            data.writeString(name);
            switch (name) {
                case "set_volume":
                case "stop_track":
                    data.writeItemStack(buf.readItemStack());
                    break;
                case "play_track":
                    data.writeItemStack(buf.readItemStack());
                    data.writeUuid(player.getUuid());
                    break;
                default: return;
            }
            server.execute(() -> {
                PlayerLookup.all(server).forEach(playerS -> ServerPlayNetworking.send(playerS, ALL_PLAYERS_CLIENT, data));
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(SET_VOLUME, (server, player, handler, buf, sender) -> {
            float volume = buf.readFloat();
            int invSlot = buf.readInt();
            server.execute(() -> {
                if (player.inventory.getStack(invSlot).hasTag()) {
                    player.inventory.getStack(invSlot).getOrCreateTag().putFloat("volume", volume);
                    player.inventory.markDirty();
                }
            });
        });
    }

}
