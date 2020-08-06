package com.ashindigo.musicexpansion.screen;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.RecordJsonParser;
import com.ashindigo.musicexpansion.handler.RecordMakerHandler;
import com.ashindigo.musicexpansion.widget.WTooltipDisc;
import com.ashindigo.musicexpansion.widget.WVerticalScrollableContainerDiscs;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import spinnery.client.screen.BaseHandledScreen;
import spinnery.widget.WInterface;
import spinnery.widget.WPanel;
import spinnery.widget.WSlot;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

// TODO Fix result slot whitelist
public class RecordMakerScreen extends BaseHandledScreen<RecordMakerHandler> {
    public RecordMakerScreen(RecordMakerHandler handler, PlayerInventory playerInventory, Text name) {
        super(handler, playerInventory, name);
        Size slotSize = Size.of(18, 18);
        WInterface mainInterface = getInterface();
        WPanel panel = mainInterface.createChild(WPanel::new, Position.of(mainInterface), Size.of(208, 214));
        panel.center();
        panel.setLabel(name); // 110
        WVerticalScrollableContainerDiscs scrollCont = mainInterface.createChild(WVerticalScrollableContainerDiscs::new, Position.of(panel).add(7, 14, 1), Size.of(198, 92));
        panel.createChild(WSlot::new, Position.of(panel, 9, 110, 2), slotSize).setInventoryNumber(RecordMakerHandler.INVENTORY).setSlotNumber(0).accept(MusicExpansion.blankRecord).setWhitelist(); // Blank record slot
        panel.createChild(WSlot::new, Position.of(panel, 45, 110, 2), slotSize).setInventoryNumber(RecordMakerHandler.INVENTORY).setSlotNumber(1);//.accept(MusicExpansion.getCraftableRecords(RecordJsonParser.isAllRecords()).toArray(new ItemStack[0])).setWhitelist(); // Result slot
        int c = 0;
        int y = 0;
        ArrayList<WTooltipDisc> row = new ArrayList<>(Collections.nCopies(9, null));
        for (ItemStack disc : MusicExpansion.getCraftableRecords(RecordJsonParser.isAllRecords())) {
            WTooltipDisc slot = new WTooltipDisc().setStack(disc).setPosition(Position.of(scrollCont).add((18 * c), 0, 2).setOffsetY((18 * y))).setSize(slotSize).setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBlockPos(handler.recordMaker.getPos());
                buf.writeItemStack(widget.getStack());
                ClientSidePacketRegistry.INSTANCE.sendToServer(MusicExpansion.CREATE_RECORD, buf);
            });
            if (!row.isEmpty()) {
                row.set(c, slot); // Set Disc to row
            }
            if (c == 8) {
                y++;
                row.removeIf(Objects::isNull);
                if (!scrollCont.contains(row.toArray(new WTooltipDisc[]{}))) {
                    scrollCont.addRow(row.toArray(new WTooltipDisc[]{}));
                    row = new ArrayList<>(Collections.nCopies(9, null));
                }
            }
            c = c == 8 ? 0 : c + 1;
        }
        row.removeIf(Objects::isNull);
        if (!scrollCont.contains(row.toArray(new WTooltipDisc[]{}))) {
            scrollCont.addRow(row.toArray(new WTooltipDisc[]{}));
        }
        WSlot.addPlayerInventory(Position.of(panel).add(9, 130, 2), Size.of(18, 18), panel);
    }

}
