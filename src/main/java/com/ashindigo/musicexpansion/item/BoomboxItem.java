package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.PacketRegistry;
import com.ashindigo.musicexpansion.handler.BoomboxHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

public class BoomboxItem extends Abstract9DiscItem {

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new BoomboxHandler(syncId, inv, player.getMainHandStack().getItem().getClass().isAssignableFrom(getClass()) ? Hand.MAIN_HAND.ordinal() : Hand.OFF_HAND.ordinal());
    }

    @Override
    public Text getDescription() {
        return new TranslatableText("desc.musicexpansion.boombox").formatted(Formatting.GRAY);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void playSelectedDisc(ItemStack stack) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString("play_track");
        buf.writeItemStack(stack);
        ClientSidePacketRegistry.INSTANCE.sendToServer(PacketRegistry.ALL_PLAYERS_SERVER, buf);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void stopSelectedDisc(ItemStack stack) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString("stop_track");
        buf.writeItemStack(stack);
        ClientSidePacketRegistry.INSTANCE.sendToServer(PacketRegistry.ALL_PLAYERS_SERVER, buf);
    }

    @Override
    public void setVolume(ItemStack stack, float volume) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString("set_volume");
        buf.writeItemStack(stack);
        ClientSidePacketRegistry.INSTANCE.sendToServer(PacketRegistry.ALL_PLAYERS_SERVER, buf);
    }
}
