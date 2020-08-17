package com.ashindigo.musicexpansion.client.screen;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.PacketRegistry;
import com.ashindigo.musicexpansion.handler.Abstract9DiscHolderHandler;
import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import com.ashindigo.musicexpansion.item.Abstract9DiscItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import spinnery.client.screen.BaseHandledScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

import java.util.Optional;

public class Abstract9DiscScreen<B extends Abstract9DiscHolderHandler> extends BaseHandledScreen<B> {

    public Abstract9DiscScreen(B handler, PlayerInventory playerInv, Text name) {
        super(handler, playerInv, name);
        WInterface mainInterface = getInterface();
        WPanel panel = mainInterface.createChild(WPanel::new).setSize(Size.of(180, 160+8));
        panel.center();
        panel.setLabel(title);
        WSlot.addPlayerInventory(Position.of(panel).add(9, 76+8, 0), MusicExpansion.SLOT_SIZE, panel);
        // Selected song indicator
        panel.createChild(WStaticImage::new, Position.of(panel).add(27, 16, 0), Size.of(18, 22)).setTexture(new Identifier(MusicExpansion.MODID, "textures/misc/selected.png"));
        for (int i = 0; i < 9; i++) {
            panel.createChild(WSlot::new, Position.of(panel).add(9 + (18 * i), 16, 0), MusicExpansion.SLOT_SIZE).setInventoryNumber(Abstract9DiscHolderHandler.INVENTORY).setSlotNumber(i);
        }
        // Play button
        panel.createChild(WButton::new, Position.of(panel).add(9, 40, 0), MusicExpansion.SLOT_SIZE).setLabel("▶").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> ((Abstract9DiscItem) handler.holder.getItem()).playSelectedDisc(playerInv.getStack(DiscHolderHelper.getSlotFromUUID(playerInv, handler.uuid))));
        // Stop button
        panel.createChild(WButton::new, Position.of(panel).add(45, 40, 0), MusicExpansion.SLOT_SIZE).setLabel("⏹").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> ((Abstract9DiscItem) handler.holder.getItem()).stopSelectedDisc(playerInv.getStack(DiscHolderHelper.getSlotFromUUID(playerInv, handler.uuid))));
        // Previous track
        panel.createChild(WButton::new, Position.of(panel).add(81, 40, 0), MusicExpansion.SLOT_SIZE).setLabel("⏮").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = Math.max(0, DiscHolderHelper.getSelectedSlot(playerInv.getStack(DiscHolderHelper.getSlotFromUUID(playerInv, handler.uuid))) - 1);
            int iSlot = DiscHolderHelper.getSlotFromUUID(playerInv, handler.uuid);
            DiscHolderHelper.setSelectedSlot(slot, iSlot);
            setActiveTrack(panel, slot);
        });
        // Next track
        panel.createChild(WButton::new, Position.of(panel).add(117, 40, 0), MusicExpansion.SLOT_SIZE).setLabel("⏭").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = Math.min(8, DiscHolderHelper.getSelectedSlot(playerInv.getStack(DiscHolderHelper.getSlotFromUUID(playerInv, handler.uuid))) + 1);
            int iSlot = DiscHolderHelper.getSlotFromUUID(playerInv, handler.uuid);
            DiscHolderHelper.setSelectedSlot(slot, iSlot);
            setActiveTrack(panel, slot);
        });
        // Random track
        panel.createChild(WButton::new, Position.of(panel).add(153, 40, 0), MusicExpansion.SLOT_SIZE).setLabel("?").setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            int slot = playerInv.player.getRandom().nextInt(9);
            DiscHolderHelper.setSelectedSlot(slot, DiscHolderHelper.getSlotFromUUID(playerInv, handler.uuid));
            setActiveTrack(panel, slot);
        });
        panel.createChild(() -> new WHorizontalSlider() {
            @Override
            public String getFormattedProgress() {
                return Math.round(getProgress()) + "%";
            }
        }, Position.of(panel).add(9, 60, 0), Size.of(162, 8)).setMin(0).setMax(100).setProgress(DiscHolderHelper.getVolume(playerInv.getStack(DiscHolderHelper.getSlotFromUUID(playerInv, handler.uuid)))).setOnProgressChange((slider) -> {
            // ItemStack tag change
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeFloat(slider.getProgress());
            buf.writeInt(DiscHolderHelper.getSlotFromUUID(playerInv, handler.uuid));
            ClientSidePacketRegistry.INSTANCE.sendToServer(PacketRegistry.SET_VOLUME, buf);
            ((Abstract9DiscItem) handler.holder.getItem()).setVolume(playerInv.getStack(DiscHolderHelper.getSlotFromUUID(playerInv, handler.uuid)), slider.getProgress());
        });

        setActiveTrack(panel, DiscHolderHelper.getSelectedSlot(handler.holder));
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
