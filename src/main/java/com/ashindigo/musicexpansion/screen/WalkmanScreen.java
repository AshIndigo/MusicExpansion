package com.ashindigo.musicexpansion.screen;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.MusicHelper;
import com.ashindigo.musicexpansion.container.WalkmanContainer;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import spinnery.client.screen.BaseHandledScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class WalkmanScreen extends BaseHandledScreen<WalkmanContainer> {


    //@SuppressWarnings("unused")
    public WalkmanScreen(WalkmanContainer container, PlayerInventory playerInv, Text title) {
        super(container, playerInv, title);
        WInterface mainInterface = getInterface();
        WPanel panel = mainInterface.createChild(WPanel::new).setSize(Size.of(180, 150));
        panel.center();
        // Selected song indicator
        panel.createChild(WStaticImage::new, Position.of(27, 6, 1), Size.of(18, 22)).setTexture(new Identifier(MusicExpansion.MODID, "textures/misc/selected.png"));
        for (int i = 0; i < 9; i++) {
            panel.createChild(WSlot::new, Position.of(panel).add(9 + (18 * i), 6, 2), Size.of(18, 18)).setInventoryNumber(WalkmanContainer.INVENTORY).setSlotNumber(i);
        }
        // Play button
        panel.createChild(WButton::new, Position.of(panel).add(9, 24 + 6, 0), Size.of(18, 18)).setLabel("▶").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> MusicHelper.playTrack(playerInv.getStack(MusicExpansion.getWalkman(playerInv))));
        // Stop button
        panel.createChild(WButton::new, Position.of(panel).add(9 + (18 * 2), 24 + 6, 0), Size.of(18, 18)).setLabel("⏹").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> MusicHelper.stopTrack());
        // Previous track
        panel.createChild(WButton::new, Position.of(panel).add(9 + (18 * 4), 24 + 6, 0), Size.of(18, 18)).setLabel("⏮").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = Math.max(0, ItemWalkman.getSelectedSlot(playerInv.getStack(MusicExpansion.getWalkman(playerInv))) - 1);
            ItemWalkman.setSelectedSlot(slot, MusicExpansion.getWalkman(playerInv));
            setActiveTrack(panel, slot);
        });
        // Next track
        panel.createChild(WButton::new, Position.of(panel).add(9 + (18 * 6), 24 + 6, 0), Size.of(18, 18)).setLabel("⏭").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = Math.min(8, ItemWalkman.getSelectedSlot(playerInv.getStack(MusicExpansion.getWalkman(playerInv))) + 1);
            ItemWalkman.setSelectedSlot(slot, MusicExpansion.getWalkman(playerInv));
            setActiveTrack(panel, slot);
        });
        // Random track
        panel.createChild(WButton::new, Position.of(panel).add(9 + (18 * 8), 24 + 6, 0), Size.of(18, 18)).setLabel("?").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = playerInv.player.getRandom().nextInt(9);
            ItemWalkman.setSelectedSlot(slot, MusicExpansion.getWalkman(playerInv));
            setActiveTrack(panel, slot);
        });
        WSlot.addPlayerInventory(Position.of(panel).add(9, 162 - 6 - 18 * 5, 2), Size.of(18, 18), panel);
        setActiveTrack(panel, ItemWalkman.getSelectedSlot(playerInv.getStack(MusicExpansion.getWalkman(playerInv))));
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
