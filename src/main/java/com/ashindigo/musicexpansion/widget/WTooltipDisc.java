package com.ashindigo.musicexpansion.widget;

import com.ashindigo.musicexpansion.helpers.DiscHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import spinnery.client.render.BaseRenderer;
import spinnery.widget.WAbstractWidget;
import spinnery.widget.WItem;
import spinnery.widget.WStaticText;
import spinnery.widget.WTooltip;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

/**
 * All because tooltipText in WTooltipItem wasn't public
 */
public class WTooltipDisc extends WItem {

    WTooltip tooltip;
    WStaticText tooltipText;

    public void updateText() {
        tooltipText.setText(DiscHelper.getDesc(stack));
    }

    @Override
    public void onFocusGained() {
        super.onFocusGained();
        updateHidden(false);
    }

    @Override
    public void onFocusReleased() {
        super.onFocusReleased();
        updateHidden(true);
    }

    @Override
    public void onMouseMoved(float mouseX, float mouseY) {
        updateWidgets();
        updateText();
        updatePositions(mouseX, mouseY);
        updateSizes();
    }

    @Override
    public void onMouseScrolled(float mouseX, float mouseY, double deltaY) {
        super.onMouseScrolled(mouseX, mouseY, deltaY);
        updateWidgets();
        updateText();
        updatePositions(mouseX, mouseY);
        updateSizes();
    }

    public void updateWidgets() {
        if (tooltip == null)
            tooltip = getInterface().createChild(WTooltip::new, Position.of(this), Size.of(this)).setHidden(true);
        if (tooltipText == null)
            tooltipText = getInterface().createChild(WStaticText::new, Position.of(tooltip).add(0, 0, 1), Size.of(this)).setHidden(true);
    }

    public void updatePositions(float mouseX, float mouseY) {
        tooltip.setPosition(Position.of(mouseX + 12, mouseY - 4, 1));
    }

    public void updateSizes() {
        tooltip.setSize(Size.of(tooltipText.getWidth() - 1, tooltipText.getHeight() - 1));
    }

    public void updateHidden(boolean hidden) {
        if (tooltip != null) {
            tooltip.setHidden(hidden);
        }
        if (tooltipText != null) {
            tooltipText.setHidden(hidden);
        }
    }

    @Override
    public void tick() {
        updateWidgets();
        updateText();
    }

    @Override
    public void draw(MatrixStack matrices, VertexConsumerProvider provider) {
        if (isHidden() || tooltip == null || tooltipText == null) return;

        tooltip.draw(matrices, provider);
        tooltipText.draw(matrices, provider);

        BaseRenderer.getAdvancedItemRenderer().renderInGui(matrices, provider, stack, getX() + 1, getY() + 1, getZ() + 5);
    }

    @Override
    public boolean isFocusedMouseListener() {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public <W extends WAbstractWidget> W setY(float y) {
        return super.setY(y);
    }

}
