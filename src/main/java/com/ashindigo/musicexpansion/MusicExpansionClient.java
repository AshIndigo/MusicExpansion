package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.item.ItemWalkman;
import com.ashindigo.musicexpansion.screen.WalkmanScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

public class MusicExpansionClient implements ClientModInitializer {

    private static KeyBinding walkmanPlay;
    private static KeyBinding walkmanStop;
    private static KeyBinding walkmanNext;
    private static KeyBinding walkmanBack;
    private static KeyBinding walkmanRand;
    private final static MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(MusicExpansion.WALKMAN_TYPE, WalkmanScreen::new);
        walkmanPlay = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanplay", GLFW.GLFW_KEY_Z));
        walkmanStop = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanstop", GLFW.GLFW_KEY_X));
        walkmanNext = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmannext", GLFW.GLFW_KEY_C));
        walkmanBack = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanback", GLFW.GLFW_KEY_V));
        walkmanRand = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanrand", GLFW.GLFW_KEY_Y));
        ClientTickCallback.EVENT.register(MusicExpansionClient::tick);

    }

    private static void tick(MinecraftClient client) {
        if (walkmanPlay.wasPressed()) {
            int iSlot = MusicExpansion.getWalkman(mc.player.inventory);
            if (iSlot > -1) {
                MusicHelper.playTrack(mc.player.inventory.getStack(iSlot));
            }
        }
        if (walkmanStop.wasPressed()) {
            MusicHelper.stopTrack();
        }
        if (walkmanNext.wasPressed()) {
            int iSlot = MusicExpansion.getWalkman(mc.player.inventory);
            if (iSlot > -1) {
                int slot = Math.min(8, ItemWalkman.getSelectedSlot(mc.player.inventory.getStack(iSlot)) + 1);
                ItemWalkman.setSelectedSlot(slot, iSlot);
                MusicDiscItem disc = MusicHelper.getDiscInSlot(mc.player.inventory.getStack(iSlot), slot);
                if (disc != null) {
                    mc.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack").append(disc.getDescription()), false);
                } else {
                    mc.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack.nothing"), false);
                }
            }
        }
        if (walkmanBack.wasPressed()) {
            int iSlot = MusicExpansion.getWalkman(mc.player.inventory);
            if (iSlot > -1) {
                int slot = Math.max(0, ItemWalkman.getSelectedSlot(mc.player.inventory.getStack(iSlot)) - 1);
                ItemWalkman.setSelectedSlot(slot, iSlot);
                MusicDiscItem disc = MusicHelper.getDiscInSlot(mc.player.inventory.getStack(iSlot), slot);
                if (disc != null) {
                    mc.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack").append(disc.getDescription()), false);
                } else {
                    mc.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack.nothing"), false);
                }
            }
        }
        if (walkmanRand.wasPressed()) {
            int iSlot = MusicExpansion.getWalkman(mc.player.inventory);
            if (iSlot > -1) {
                int slot = mc.player.getRandom().nextInt(9);
                ItemWalkman.setSelectedSlot(slot, iSlot);
                MusicDiscItem disc = MusicHelper.getDiscInSlot(mc.player.inventory.getStack(iSlot), slot);
                if (disc != null) {
                    mc.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack").append(disc.getDescription()), false);
                } else {
                    mc.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack.nothing"), false);
                }
            }
        }
    }


    public KeyBinding registerKeybind(String name, int key) {
        return new KeyBinding("key.musicexpansion." + name, InputUtil.Type.KEYSYM, key, "category.musicexpansion.binds");
    }
}
