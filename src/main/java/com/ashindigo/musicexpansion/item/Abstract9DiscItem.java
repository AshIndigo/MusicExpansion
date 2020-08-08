package com.ashindigo.musicexpansion.item;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.helpers.DiscHolderHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class Abstract9DiscItem extends Item implements ExtendedScreenHandlerFactory {

    public Abstract9DiscItem() {
        super(new Item.Settings().maxCount(1).group(MusicExpansion.MUSIC_GROUP));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(DiscHolderHelper.getDiscHolderSlot(getClass(), player.inventory));
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getTranslationKey());
    }
}
