package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.client.CustomDiscModelPredicateProvider;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import com.ashindigo.musicexpansion.screen.RecordMakerScreen;
import com.ashindigo.musicexpansion.screen.WalkmanScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class MusicExpansionClient implements ClientModInitializer {

    private static KeyBinding walkmanPlay;
    private static KeyBinding walkmanStop;
    private static KeyBinding walkmanNext;
    private static KeyBinding walkmanBack;
    private static KeyBinding walkmanRand;

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(MusicExpansion.WALKMAN_TYPE, WalkmanScreen::new);
        ScreenRegistry.register(MusicExpansion.RECORDMAKER_TYPE, RecordMakerScreen::new);
        walkmanPlay = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanplay", GLFW.GLFW_KEY_UP));
        walkmanStop = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanstop", GLFW.GLFW_KEY_DOWN));
        walkmanNext = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmannext", GLFW.GLFW_KEY_RIGHT));
        walkmanBack = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanback", GLFW.GLFW_KEY_LEFT));
        walkmanRand = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanrand", GLFW.GLFW_KEY_RIGHT_ALT));
        ClientTickEvents.END_CLIENT_TICK.register(MusicExpansionClient::tick);
        ClientSidePacketRegistry.INSTANCE.register(MusicExpansion.ALL_RECORDS,
                (packetContext, attachedData) -> RecordJsonParser.setAllRecords(attachedData.readBoolean()));
        FabricModelPredicateProviderRegistry.register(MusicExpansion.customDisc, new Identifier(MusicExpansion.MODID, "custom_disc_model"), new CustomDiscModelPredicateProvider());
    }

    // client.player should never be null
    private static void tick(MinecraftClient client) {
        if (walkmanPlay.wasPressed()) {
            walkmanPlayDisc(client);
        }
        if (walkmanStop.wasPressed()) {
            MusicHelper.stopTrack();
        }
        if (walkmanNext.wasPressed()) {
            walkmanNextDisc(client);
        }
        if (walkmanBack.wasPressed()) {
            walkmanPrevDisc(client);
        }
        if (walkmanRand.wasPressed()) {
            randomDisc(client);
        }
    }

    private static void walkmanPlayDisc(MinecraftClient client) {
        int iSlot = DiscHelper.getWalkman(client.player.inventory);
        if (iSlot > -1) {
            MusicHelper.playTrack(client.player.inventory.getStack(iSlot));
        }
    }

    private static void randomDisc(MinecraftClient client) {
        int iSlot = DiscHelper.getWalkman(client.player.inventory);
        if (iSlot > -1) {
            int slot = client.player.getRandom().nextInt(9);
            ItemWalkman.setSelectedSlot(slot, iSlot);
            MusicDiscItem disc = MusicHelper.getDiscInSlot(client.player.inventory.getStack(iSlot), slot);
            if (disc != null) {
                client.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack").append(disc.getDescription()), false);
            } else {
                client.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack.nothing"), false);
            }
        }
    }

    private static void walkmanPrevDisc(MinecraftClient client) {
        int iSlot = DiscHelper.getWalkman(client.player.inventory);
        if (iSlot > -1) {
            int slot = Math.max(0, ItemWalkman.getSelectedSlot(client.player.inventory.getStack(iSlot)) - 1);
            ItemWalkman.setSelectedSlot(slot, iSlot);
            MusicDiscItem disc = MusicHelper.getDiscInSlot(client.player.inventory.getStack(iSlot), slot);
            if (disc != null) {
                client.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack").append(disc.getDescription()), false);
            } else {
                client.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack.nothing"), false);
            }
        }
    }

    private static void walkmanNextDisc(MinecraftClient client) {
        int iSlot = DiscHelper.getWalkman(client.player.inventory);
        if (iSlot > -1) {
            int slot = Math.min(8, ItemWalkman.getSelectedSlot(client.player.inventory.getStack(iSlot)) + 1);
            ItemWalkman.setSelectedSlot(slot, iSlot);
            MusicDiscItem disc = MusicHelper.getDiscInSlot(client.player.inventory.getStack(iSlot), slot);
            if (disc != null) {
                client.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack").append(disc.getDescription()), false);
            } else {
                client.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack.nothing"), false);
            }
        }
    }


    public KeyBinding registerKeybind(String name, int key) {
        return new KeyBinding("key.musicexpansion." + name, InputUtil.Type.KEYSYM, key, "category.musicexpansion.binds");
    }
}
