package com.ashindigo.musicexpansion.mixin;

import com.ashindigo.musicexpansion.MusicExpansionResourcePack;
import com.google.common.collect.Lists;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {

    @Inject(method = "createResourcePacks()Ljava/util/List;", at = @At(value = "RETURN"), cancellable = true)
    public void createResourcePacks(CallbackInfoReturnable<List<ResourcePack>> info) {
        List<ResourcePack> list = Lists.newArrayList(info.getReturnValue());
        list.add(new MusicExpansionResourcePack());
        info.setReturnValue(list);
    }
}
