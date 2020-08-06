package com.ashindigo.musicexpansion.screen;

import com.ashindigo.musicexpansion.DiscHelper;
import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.MusicHelper;
import com.ashindigo.musicexpansion.handler.WalkmanHandler;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import spinnery.client.screen.BaseHandledScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

import java.util.Optional;

public class WalkmanScreen extends BaseHandledScreen<WalkmanHandler> {

    public WalkmanScreen(WalkmanHandler container, PlayerInventory playerInv, Text title) {
        super(container, playerInv, title);
        WInterface mainInterface = getInterface();
        WPanel panel = mainInterface.createChild(WPanel::new).setSize(Size.of(180, 160));
        panel.center();
        panel.setLabel(title);
        WSlot.addPlayerInventory(Position.of(panel).add(9, 76, 0), Size.of(18, 18), panel);
        // Selected song indicator
        panel.createChild(WStaticImage::new, Position.of(27, 16, 0), Size.of(18, 22)).setTexture(new Identifier(MusicExpansion.MODID, "textures/misc/selected.png"));
        for (int i = 0; i < 9; i++) {
            panel.createChild(WSlot::new, Position.of(panel).add(9 + (18 * i), 16, 0), Size.of(18, 18)).setInventoryNumber(WalkmanHandler.INVENTORY).setSlotNumber(i);
        }
        // Play button
        panel.createChild(WButton::new, Position.of(panel).add(9, 40, 0), Size.of(18, 18)).setLabel("▶").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> MusicHelper.playTrack(playerInv.getStack(DiscHelper.getWalkman(playerInv))));
        // Stop button
        panel.createChild(WButton::new, Position.of(panel).add(45, 40, 0), Size.of(18, 18)).setLabel("⏹").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> MusicHelper.stopTrack());
        // Previous track
        panel.createChild(WButton::new, Position.of(panel).add(81, 40, 0), Size.of(18, 18)).setLabel("⏮").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = Math.max(0, ItemWalkman.getSelectedSlot(playerInv.getStack(DiscHelper.getWalkman(playerInv))) - 1);
            ItemWalkman.setSelectedSlot(slot, DiscHelper.getWalkman(playerInv));
            setActiveTrack(panel, slot);
        });
        // Next track
        panel.createChild(WButton::new, Position.of(panel).add(117, 40, 0), Size.of(18, 18)).setLabel("⏭").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = Math.min(8, ItemWalkman.getSelectedSlot(playerInv.getStack(DiscHelper.getWalkman(playerInv))) + 1);
            ItemWalkman.setSelectedSlot(slot, DiscHelper.getWalkman(playerInv));
            setActiveTrack(panel, slot);
        });
        // Random track
        panel.createChild(WButton::new, Position.of(panel).add(153, 40, 0), Size.of(18, 18)).setLabel("?").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = playerInv.player.getRandom().nextInt(9);
            ItemWalkman.setSelectedSlot(slot, DiscHelper.getWalkman(playerInv));
            setActiveTrack(panel, slot);
        });
        setActiveTrack(panel, ItemWalkman.getSelectedSlot(playerInv.getStack(DiscHelper.getWalkman(playerInv))));
    }

    private void setActiveTrack(WPanel panel, int selectedSlot) {
        Optional<WAbstractWidget> slot = panel.getWidgets().stream().filter(wAbstractWidget -> {
            if (wAbstractWidget instanceof WSlot) {
                if (((WSlot) wAbstractWidget).getSlotNumber() == selectedSlot) {
                    return ((WSlot) wAbstractWidget).getInventoryNumber() == WalkmanHandler.INVENTORY;
                }
            }
            return false;
        }).findFirst();
        slot.ifPresent(abstractWidget -> {
            Optional<WAbstractWidget> image = panel.getWidgets().stream().filter(wAbstractWidget -> wAbstractWidget instanceof WStaticImage).findFirst();
            image.ifPresent(wAbstractWidget -> wAbstractWidget.setPosition(abstractWidget.getPosition().setZ(1)));
        });
    }
}
