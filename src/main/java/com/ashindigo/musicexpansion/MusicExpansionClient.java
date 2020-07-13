package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.screen.WalkmanScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AmbientSoundLoops;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.network.PacketByteBuf;

public class MusicExpansionClient implements ClientModInitializer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static WalkmanMovingSound sound;
    private boolean isPlaying;

    @Override
    public void onInitializeClient() {
//        ClientSidePacketRegistry.INSTANCE.register(MusicExpansion.PLAYDISK_PACKET,
//                (packetContext, attachedData) -> {
//                    ItemStack stack = attachedData.readItemStack();
//                    packetContext.getTaskQueue().execute(() -> {
//                        if (!isPlaying || !mc.getSoundManager().isPlaying(sound)) {
//                            mc.inGameHud.setRecordPlayingOverlay(((MusicDiscItem) stack.getItem()).getDescription());
//                            sound = new WalkmanMovingSound(((MusicDiscItem) stack.getItem()).getSound(), mc.player);
//                            mc.getSoundManager().play(sound);
//                            isPlaying = true;
//                        }
//                    });
//                });
//        ClientSidePacketRegistry.INSTANCE.register(MusicExpansion.STOPDISK_PACKET,
//                (packetContext, attachedData) -> packetContext.getTaskQueue().execute(() -> {
//                    MinecraftClient.getInstance().getSoundManager().stop(sound);
//                }));

        ScreenRegistry.register(MusicExpansion.WALKMAN_TYPE, WalkmanScreen::new);
    }
}
