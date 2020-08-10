package com.ashindigo.musicexpansion;

import com.ashindigo.musicexpansion.accessor.WorldRendererAccessor;
import com.ashindigo.musicexpansion.handler.BoomboxHandler;
import com.ashindigo.musicexpansion.handler.WalkmanHandler;
import com.ashindigo.musicexpansion.helpers.DiscHelper;
import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import com.ashindigo.musicexpansion.helpers.MusicHelper;
import com.ashindigo.musicexpansion.screen.BoomboxScreen;
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
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

public class MusicExpansionClient implements ClientModInitializer {

    private static KeyBinding walkmanPlay;
    private static KeyBinding walkmanStop;
    private static KeyBinding walkmanNext;
    private static KeyBinding walkmanBack;
    private static KeyBinding walkmanRand;

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(MusicExpansion.WALKMAN_TYPE, (WalkmanHandler handler, PlayerInventory playerInv, Text title) -> new WalkmanScreen(handler, playerInv, title, MusicHelper::playWalkmanTrack));
        ScreenRegistry.register(MusicExpansion.BOOMBOX_TYPE, (BoomboxHandler handler, PlayerInventory playerInv, Text title) -> new BoomboxScreen(handler, playerInv, title, MusicHelper::playBoomboxTrack));
        ScreenRegistry.register(MusicExpansion.RECORD_MAKER_TYPE, RecordMakerScreen::new);
        walkmanPlay = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanplay", GLFW.GLFW_KEY_UP));
        walkmanStop = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanstop", GLFW.GLFW_KEY_DOWN));
        walkmanNext = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmannext", GLFW.GLFW_KEY_RIGHT));
        walkmanBack = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanback", GLFW.GLFW_KEY_LEFT));
        walkmanRand = KeyBindingHelper.registerKeyBinding(registerKeybind("walkmanrand", GLFW.GLFW_KEY_RIGHT_ALT));
        ClientTickEvents.END_CLIENT_TICK.register(MusicExpansionClient::tick);
        registerPackets();
        FabricModelPredicateProviderRegistry.register(MusicExpansion.customDisc, new Identifier(MusicExpansion.MODID, "custom_disc"), (stack, world, entity) -> 1F * MusicExpansion.tracks.indexOf(Identifier.tryParse(stack.getOrCreateTag().getString("track"))));
    }

    private void registerPackets() {
        // Get whether or not to allow all configs
        ClientSidePacketRegistry.INSTANCE.register(MusicExpansion.ALL_RECORDS,
                (packetContext, attachedData) -> RecordJsonParser.setAllRecords(attachedData.readBoolean()));
        // Play track
        ClientSidePacketRegistry.INSTANCE.register(MusicExpansion.PLAY_JUKEBOX_TRACK, (packetContext, attachedData) -> {
            ItemStack disc = attachedData.readItemStack();
            BlockPos songPosition = attachedData.readBlockPos();
            MinecraftClient mc = MinecraftClient.getInstance();
            packetContext.getTaskQueue().execute(() -> {
                if (mc.player != null) {
                    if (!disc.isEmpty()) {
                        SoundEvent event = DiscHelper.getEvent(disc);
                        if (event != null) {
                            mc.inGameHud.setRecordPlayingOverlay(DiscHelper.getDesc(disc));
                            SoundInstance soundInstance = PositionedSoundInstance.record(event, songPosition.getX(), songPosition.getY(), songPosition.getZ());
                            ((WorldRendererAccessor) mc.worldRenderer).musicexpansion_getPlayingSongs().put(songPosition, soundInstance);
                            mc.getSoundManager().play(soundInstance);
                        }
                    }
                }
            });
        });
        // Sync sound events
        ClientSidePacketRegistry.INSTANCE.register(MusicExpansion.SYNC_EVENTS, (ctx, buf) -> {
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                Identifier id = buf.readIdentifier();
                if (!Registry.SOUND_EVENT.containsId(id)) {
                    Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
                    MusicExpansion.tracks.add(id);
                }
            }
        });
    }


    // client.player should never be null
    private static void tick(MinecraftClient client) {
        if (walkmanPlay.wasPressed()) {
            walkmanPlayDisc(client);
        }
        if (walkmanStop.wasPressed()) {
            walkmanStopDisc(client);
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
        if (client.player != null) {
            int iSlot = DiscHolderHelper.getActiveDiscHolderSlot(client.player.inventory);
            if (iSlot > -1) {
                MusicHelper.playWalkmanTrack(client.player.inventory.getStack(iSlot));
            }
        }
    }

    private static void walkmanStopDisc(MinecraftClient client) {
        if (client.player != null) {
            int iSlot = DiscHolderHelper.getActiveDiscHolderSlot(client.player.inventory);
            if (iSlot > -1) {
                MusicHelper.stopTrack(client.player.inventory.getStack(iSlot));
            }
        }
    }


    private static void randomDisc(MinecraftClient client) {
        if (client.player != null) {
            int iSlot = DiscHolderHelper.getActiveDiscHolderSlot(client.player.inventory);
            if (iSlot > -1) {
                int slot = client.player.getRandom().nextInt(9);
                DiscHolderHelper.setSelectedSlot(slot, iSlot);
                sendCurrentDiscMessage(client, iSlot, slot);
            }
        }
    }

    private static void walkmanPrevDisc(MinecraftClient client) {
        if (client.player != null) {
            int iSlot = DiscHolderHelper.getActiveDiscHolderSlot(client.player.inventory);
            if (iSlot > -1) {
                int slot = Math.max(0, DiscHolderHelper.getSelectedSlot(client.player.inventory.getStack(iSlot)) - 1);
                DiscHolderHelper.setSelectedSlot(slot, iSlot);
                sendCurrentDiscMessage(client, iSlot, slot);
            }
        }
    }

    private static void walkmanNextDisc(MinecraftClient client) {
        if (client.player != null) {
            int iSlot = DiscHolderHelper.getActiveDiscHolderSlot(client.player.inventory);
            if (iSlot > -1) {
                int slot = Math.min(8, DiscHolderHelper.getSelectedSlot(client.player.inventory.getStack(iSlot)) + 1);
                DiscHolderHelper.setSelectedSlot(slot, iSlot);
                sendCurrentDiscMessage(client, iSlot, slot);
            }
        }
    }

    private static void sendCurrentDiscMessage(MinecraftClient client, int iSlot, int slot) {
        if (client.player != null) {
            Text desc = DiscHelper.getDesc(DiscHolderHelper.getDiscInSlot(client.player.inventory.getStack(iSlot), slot));
            if (!desc.equals(new LiteralText(""))) {
                client.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack").append(desc), false);
            } else {
                client.player.sendMessage(new TranslatableText("text.musicexpansion.currenttrack.nothing"), false);
            }
        }
    }


    public KeyBinding registerKeybind(String name, int key) {
        return new KeyBinding("key.musicexpansion." + name, InputUtil.Type.KEYSYM, key, "category.musicexpansion.binds");
    }
}
