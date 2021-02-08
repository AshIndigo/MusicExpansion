package com.ashindigo.musicexpansion.mixin;

import com.ashindigo.musicexpansion.MusicExpansion;
import me.shedaniel.rei.RoughlyEnoughItemsCore;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/debug/DebugRenderer;reset()V"))
    public void musicexpansion_onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        RoughlyEnoughItemsCore.syncRecipes(null);
        if (!MusicExpansion.tracks.equals(MusicExpansion.tracksOrig)) {
            MinecraftClient.getInstance().player.sendMessage(new TranslatableText("text.musicexpansion.reload_textures"), false);
        }
    }
}
