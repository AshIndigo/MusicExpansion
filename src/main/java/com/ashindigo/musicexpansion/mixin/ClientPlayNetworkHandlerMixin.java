package com.ashindigo.musicexpansion.mixin;

import me.shedaniel.rei.RoughlyEnoughItemsCore;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/debug/DebugRenderer;reset()V"))
    public void musicexpansion_onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        RoughlyEnoughItemsCore.syncRecipes(null);
    }
}
