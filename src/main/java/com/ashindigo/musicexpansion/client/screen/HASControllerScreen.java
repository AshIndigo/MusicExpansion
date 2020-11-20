//package com.ashindigo.musicexpansion.client.screen;
//
//import com.ashindigo.musicexpansion.MusicExpansion;
//import com.ashindigo.musicexpansion.handler.Abstract9DiscHolderHandler;
//import com.ashindigo.musicexpansion.handler.HASControllerHandler;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.text.Text;
//import net.minecraft.util.Identifier;
//import spinnery.client.screen.BaseHandledScreen;
//import spinnery.widget.*;
//import spinnery.widget.api.Position;
//import spinnery.widget.api.Size;
//
//import java.util.Optional;
//
//// Show how many speakers there are?
//public class HASControllerScreen extends BaseHandledScreen<HASControllerHandler> {
//
//    public HASControllerScreen(HASControllerHandler handler, PlayerInventory playerInv, Text name) {
//        super(handler, playerInv, name);
//        WInterface mainInterface = getInterface();
//        WPanel panel = mainInterface.createChild(WPanel::new).setSize(Size.of(180, 160));
//        panel.center();
//        panel.setLabel(title);
//        WSlot.addPlayerInventory(Position.of(panel).add(9, 76, 0), MusicExpansion.SLOT_SIZE, panel);
//        // Selected song indicator
//        panel.createChild(WStaticImage::new, Position.of(27, 16, 0), Size.of(18, 22)).setTexture(new Identifier(MusicExpansion.MODID, "textures/misc/selected.png"));
//        for (int i = 0; i < 9; i++) {
//            panel.createChild(WSlot::new, Position.of(panel).add(9 + (18 * i), 16, 0), MusicExpansion.SLOT_SIZE).setInventoryNumber(Abstract9DiscHolderHandler.INVENTORY).setSlotNumber(i);
//        }
//        // Play button
//        panel.createChild(WButton::new, Position.of(panel).add(9, 40, 0), MusicExpansion.SLOT_SIZE).setLabel("▶").setOnMouseClicked(this::playTrack);
//        // Stop button
//        panel.createChild(WButton::new, Position.of(panel).add(45, 40, 0), MusicExpansion.SLOT_SIZE).setLabel("⏹").setOnMouseClicked(this::stopTrack);
//        // Previous track
//        panel.createChild(WButton::new, Position.of(panel).add(81, 40, 0), MusicExpansion.SLOT_SIZE).setLabel("⏮").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
//            int slot = Math.max(0, handler.controller.getSelectedSlot() - 1);
//            handler.controller.setSelectedSlot(slot);
//            setActiveTrack(panel, slot);
//        });
//        // Next track
//        panel.createChild(WButton::new, Position.of(panel).add(117, 40, 0), MusicExpansion.SLOT_SIZE).setLabel("⏭").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
//            int slot = Math.min(8, handler.controller.getSelectedSlot() + 1);
//            handler.controller.setSelectedSlot(slot);
//            setActiveTrack(panel, slot);
//        });
//        // Random track
//        panel.createChild(WButton::new, Position.of(panel).add(153, 40, 0), MusicExpansion.SLOT_SIZE).setLabel("?").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
//            int slot = playerInv.player.getRandom().nextInt(9);
//            handler.controller.setSelectedSlot(slot);
//            setActiveTrack(panel, slot);
//        });
//        // Enter ID
//        panel.createChild(WTextField::new, Position.of(panel).add(9, 58, 0), Size.of(20, 10));
//        setActiveTrack(panel, handler.controller.getSelectedSlot());
//
//    }
//
//    private void playTrack(WAbstractWidget widget, float mouseX, float mouseY, int mouseButton) {
//
//    }
//
//    private void stopTrack(WAbstractWidget widget, float mouseX, float mouseY, int mouseButton) {
//    }
//
//    public void setActiveTrack(WPanel panel, int selectedSlot) {
//        Optional<WAbstractWidget> slot = panel.getWidgets().stream().filter(wAbstractWidget -> {
//            if (wAbstractWidget instanceof WSlot) {
//                if (((WSlot) wAbstractWidget).getSlotNumber() == selectedSlot) {
//                    return ((WSlot) wAbstractWidget).getInventoryNumber() == Abstract9DiscHolderHandler.INVENTORY;
//                }
//            }
//            return false;
//        }).findFirst();
//        slot.ifPresent(abstractWidget -> {
//            Optional<WAbstractWidget> image = panel.getWidgets().stream().filter(wAbstractWidget -> wAbstractWidget instanceof WStaticImage).findFirst();
//            image.ifPresent(wAbstractWidget -> wAbstractWidget.setPosition(abstractWidget.getPosition().setZ(1)));
//        });
//    }
//}
