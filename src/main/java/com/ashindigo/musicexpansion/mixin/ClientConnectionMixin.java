package com.ashindigo.musicexpansion.mixin;

import com.ashindigo.musicexpansion.MusicExpansion;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "disconnect(Lnet/minecraft/text/Text;)V", at = @At("RETURN"))
    public void musicexpansion_disconnect(Text reason, CallbackInfo info) {
        MusicExpansion.tracks = new ArrayList<>(MusicExpansion.tracksOrig);
    }
}
