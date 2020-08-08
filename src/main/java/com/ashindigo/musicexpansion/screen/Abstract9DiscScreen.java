package com.ashindigo.musicexpansion.screen;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.handler.Abstract9DiscHolderHandler;
import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import com.ashindigo.musicexpansion.helpers.MusicHelper;
import com.ashindigo.musicexpansion.item.Abstract9DiscItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import spinnery.client.screen.BaseHandledScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

import java.util.Optional;
import java.util.function.Consumer;

// TODO Seperate from walkman
public class Abstract9DiscScreen<B extends Abstract9DiscHolderHandler> extends BaseHandledScreen<B> {

    public static final Size SLOT_SIZE = Size.of(18,18);

    public Abstract9DiscScreen(B handler, PlayerInventory playerInv, Text name, Class<? extends Abstract9DiscItem> clazz, Consumer<ItemStack> playButton) {
        super(handler, playerInv, name);
        WInterface mainInterface = getInterface();
        WPanel panel = mainInterface.createChild(WPanel::new).setSize(Size.of(180, 160));
        panel.center();
        panel.setLabel(title);
        WSlot.addPlayerInventory(Position.of(panel).add(9, 76, 0), SLOT_SIZE, panel);
        // Selected song indicator
        panel.createChild(WStaticImage::new, Position.of(27, 16, 0), Size.of(18, 22)).setTexture(new Identifier(MusicExpansion.MODID, "textures/misc/selected.png"));
        for (int i = 0; i < 9; i++) {
            panel.createChild(WSlot::new, Position.of(panel).add(9 + (18 * i), 16, 0), SLOT_SIZE).setInventoryNumber(Abstract9DiscHolderHandler.INVENTORY).setSlotNumber(i);
        }
        // Play button
        panel.createChild(WButton::new, Position.of(panel).add(9, 40, 0), SLOT_SIZE).setLabel("▶").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> playButton.accept(playerInv.getStack(DiscHolderHelper.getDiscHolderSlot(clazz, playerInv))));
        // Stop button
        panel.createChild(WButton::new, Position.of(panel).add(45, 40, 0), SLOT_SIZE).setLabel("⏹").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> MusicHelper.stopTrack());
        // Previous track
        panel.createChild(WButton::new, Position.of(panel).add(81, 40, 0), SLOT_SIZE).setLabel("⏮").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = Math.max(0, DiscHolderHelper.getSelectedSlot(playerInv.getStack(DiscHolderHelper.getDiscHolderSlot(clazz, playerInv))) - 1);
            DiscHolderHelper.setSelectedSlot(slot, DiscHolderHelper.getDiscHolderSlot(clazz, playerInv));
            setActiveTrack(panel, slot);
        });
        // Next track
        panel.createChild(WButton::new, Position.of(panel).add(117, 40, 0), SLOT_SIZE).setLabel("⏭").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = Math.min(8, DiscHolderHelper.getSelectedSlot(playerInv.getStack(DiscHolderHelper.getDiscHolderSlot(clazz, playerInv))) + 1);
            DiscHolderHelper.setSelectedSlot(slot, DiscHolderHelper.getDiscHolderSlot(clazz, playerInv));
            setActiveTrack(panel, slot);
        });
        // Random track
        panel.createChild(WButton::new, Position.of(panel).add(153, 40, 0), SLOT_SIZE).setLabel("?").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = playerInv.player.getRandom().nextInt(9);
            DiscHolderHelper.setSelectedSlot(slot, DiscHolderHelper.getDiscHolderSlot(clazz, playerInv));
            setActiveTrack(panel, slot);
        });
        setActiveTrack(panel, DiscHolderHelper.getSelectedSlot(playerInv.getStack(DiscHolderHelper.getDiscHolderSlot(clazz, playerInv))));
    }

    public void setActiveTrack(WPanel panel, int selectedSlot) {
        Optional<WAbstractWidget> slot = panel.getWidgets().stream().filter(wAbstractWidget -> {
            if (wAbstractWidget instanceof WSlot) {
                if (((WSlot) wAbstractWidget).getSlotNumber() == selectedSlot) {
                    return ((WSlot) wAbstractWidget).getInventoryNumber() == Abstract9DiscHolderHandler.INVENTORY;
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
