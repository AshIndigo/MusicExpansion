package com.ashindigo.musicexpansion.client;

import com.ashindigo.musicexpansion.entity.DiscRackEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class DiskRackRenderer extends BlockEntityRenderer<DiscRackEntity> {

    public DiskRackRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(DiscRackEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        DefaultedList<ItemStack> items = entity.getItems();
        if (!entity.isEmpty()) {
            matrices.push();
            matrices.scale(.5F, .5F, .5F);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));
            matrices.translate(-1.035, .35, .125); // .125
            for (int i = 0, itemsSize = items.size(); i < itemsSize; i++) {
                if (i > 0) {
                    matrices.translate(0, 0, .215);
                }
                MinecraftClient.getInstance().getItemRenderer().renderItem(items.get(i), ModelTransformation.Mode.FIXED, WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up()), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
            }
            matrices.pop();
        }
    }
}
