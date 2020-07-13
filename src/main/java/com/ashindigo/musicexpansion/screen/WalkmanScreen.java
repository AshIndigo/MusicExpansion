package com.ashindigo.musicexpansion.screen;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.WalkmanMovingSound;
import com.ashindigo.musicexpansion.container.WalkmanContainer;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import spinnery.client.screen.BaseHandledScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class WalkmanScreen extends BaseHandledScreen<WalkmanContainer> {

    static WalkmanMovingSound sound;
    static boolean isPlaying;

    public WalkmanScreen(WalkmanContainer container, PlayerInventory playerInv, Text title) {
        super(container, playerInv, title);
        WInterface mainInterface = getInterface();
        WPanel panel = mainInterface.createChild(WPanel::new).setSize(Size.of(180, 150));
        panel.center();
        WStaticImage selected = panel.createChild(WStaticImage::new, Position.of(27, 6, 1), Size.of(18, 22)).setTexture(new Identifier(MusicExpansion.MODID, "textures/misc/selected.png"));
        for (int i = 0; i < 9; i++) {
            panel.createChild(WSlot::new, Position.of(panel).add(9 + (18 * i), 6, 2), Size.of(18, 18)).setInventoryNumber(WalkmanContainer.INVENTORY).setSlotNumber(i);
        }
        setActiveTrack(panel, ItemWalkman.getSelectedSlot(playerInv.getStack(container.slot)));
        WButton play = panel.createChild(WButton::new, Position.of(panel).add(9, 24 + 6, 0), Size.of(18, 18)).setLabel("▶").setOnMouseClicked
                ((widget, mouseX, mouseY, mouseButton) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (!isPlaying || !mc.getSoundManager().isPlaying(sound)) {
                if (!container.getInventory(1).getStack(ItemWalkman.getSelectedSlot(playerInv.getStack(container.slot))).isEmpty()) {
                    MusicDiscItem currentDisc = ((MusicDiscItem) container.getInventory(1).getStack(ItemWalkman.getSelectedSlot(playerInv.getStack(container.slot))).getItem());
                    mc.inGameHud.setRecordPlayingOverlay(currentDisc.getDescription());
                    sound = new WalkmanMovingSound(currentDisc.getSound(), mc.player);
                    mc.getSoundManager().play(sound);
                    isPlaying = true;
                }
            }
        });
        WButton stop = panel.createChild(WButton::new, Position.of(panel).add(9 + (18 * 2), 24 + 6, 0), Size.of(18, 18)).setLabel("⏹").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            //MusicDiscItem currentDisc = ((MusicDiscItem) container.getInventory(1).getStack(ItemWalkman.getSelectedSlot(container.stack)).getItem());
            MinecraftClient.getInstance().getSoundManager().stop(sound);
            isPlaying = false;
        });
        WButton previousSong = panel.createChild(WButton::new, Position.of(panel).add(9 + (18 * 4), 24 + 6, 0), Size.of(18, 18)).setLabel("⏮").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = Math.max(0, ItemWalkman.getSelectedSlot(playerInv.getStack(container.slot)) - 1);
            ItemWalkman.setSelectedSlot(playerInv.getStack(container.slot), slot, playerInv.getSlotWithStack(playerInv.getStack(container.slot)));
            setActiveTrack(panel, slot);
        });
        WButton nextSong = panel.createChild(WButton::new, Position.of(panel).add(9 + (18 * 6), 24 + 6, 0), Size.of(18, 18)).setLabel("⏭").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = Math.min(8, ItemWalkman.getSelectedSlot(playerInv.getStack(container.slot)) + 1);
            ItemWalkman.setSelectedSlot(playerInv.getStack(container.slot), slot, playerInv.getSlotWithStack(playerInv.getStack(container.slot)));
            setActiveTrack(panel, slot);
        });
        WButton random = panel.createChild(WButton::new, Position.of(panel).add(9 + (18 * 8), 24 + 6, 0), Size.of(18, 18)).setLabel("?").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = playerInv.player.getRandom().nextInt(9);
            ItemWalkman.setSelectedSlot(playerInv.getStack(container.slot), slot, playerInv.getSlotWithStack(playerInv.getStack(container.slot)));
            setActiveTrack(panel, slot);
        });
        WSlot.addPlayerInventory(Position.of(panel).add(9, 162 - 6 - 18 * 5, 2), Size.of(18, 18), panel);
    }

    private void setActiveTrack(WPanel panel, int selectedSlot) {
        WSlot slot = (WSlot) panel.getWidgets().stream().filter(wAbstractWidget -> {
            if (wAbstractWidget instanceof WSlot) {
                return ((WSlot) wAbstractWidget).getSlotNumber() == selectedSlot;
            }
            return false;
        }).findFirst().get();
        WStaticImage image = (WStaticImage) panel.getWidgets().stream().filter(wAbstractWidget -> wAbstractWidget instanceof WStaticImage).findFirst().get();
        image.setPosition(slot.getPosition().setZ(1));
    }
}
