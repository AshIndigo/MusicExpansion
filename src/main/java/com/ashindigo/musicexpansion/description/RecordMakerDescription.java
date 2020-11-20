package com.ashindigo.musicexpansion.description;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.PacketRegistry;
import com.ashindigo.musicexpansion.RecordJsonParser;
import com.ashindigo.musicexpansion.helpers.DiscHelper;
import com.ashindigo.musicexpansion.widget.WRecordButton;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class RecordMakerDescription extends SyncedGuiDescription {

    public RecordMakerDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
        super(MusicExpansion.RECORD_MAKER_HANDLER_TYPE, syncId, playerInventory, getBlockInventory(ctx, 2), getBlockPropertyDelegate(ctx));
        WPlainPanel root = new WPlainPanel();
        root.setSize(162, 214);
        WGridPanel subRoot = new WGridPanel();
        WListPanel<ItemStack, WRecordButton> records = new WListPanel<>(MusicExpansion.getCraftableRecords(RecordJsonParser.isAllRecords()), WRecordButton::new, (stack, wButton) -> {
            wButton.setIcon(new ItemIcon(stack));
            wButton.setLabel(DiscHelper.getDesc(stack));
            wButton.setSize(20, 16);
            wButton.setRecord(stack);
            wButton.setOnClick(() -> {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                final BlockPos[] pos = new BlockPos[1]; // Hack
                ctx.run((world, blockPos) -> {
                    pos[0] = blockPos;
                });
                buf.writeBlockPos(pos[0]);
                buf.writeItemStack(wButton.getRecord());
                ClientSidePacketRegistry.INSTANCE.sendToServer(PacketRegistry.CREATE_RECORD, buf);
            });
            wButton.validate(this);
        });
        records.setListItemHeight(16);
        root.add(records, 0, 16);
        records.setSize(162, 160);
        subRoot.add(new WLabel(new TranslatableText("text.musicexpansion.blank_disk").append(":")), 0, 5);
        subRoot.add(new WLabel(new TranslatableText("text.musicexpansion.result").append(":")), 6, 5);
        subRoot.add(new WItemSlot(blockInventory, 0, 1, 1, false), 4,5);
        subRoot.add(new WItemSlot(blockInventory, 1, 1, 1, false), 8,5);
        subRoot.add(new WPlayerInvPanel(playerInventory, true), 0, 6);
        root.add(subRoot, 0, 90);
        setRootPanel(root);
        records.validate(this);
        subRoot.validate(this);
    }
}
