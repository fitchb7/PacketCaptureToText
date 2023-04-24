package com.hamusuke.packetcap.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScalableCheckbox extends AbstractButton {
    protected static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
    protected static final int TEXT_COLOR = 14737632;
    protected boolean selected;
    protected final boolean showLabel;

    public ScalableCheckbox(int x, int y, int width, int height, Component component, boolean selected) {
        this(x, y, width, height, component, selected, true);
    }

    public ScalableCheckbox(int x, int y, int width, int height, Component component, boolean selected, boolean showLabel) {
        super(x, y, width, height, component);
        this.selected = selected;
        this.showLabel = showLabel;
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
    }

    public boolean selected() {
        return this.selected;
    }

    public void setSelected(boolean flag) {
        this.selected = flag;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput p_259858_) {
        p_259858_.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                p_259858_.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
            } else {
                p_259858_.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
            }
        }
    }

    @Override
    public void renderWidget(PoseStack p_275468_, int p_275505_, int p_275674_, float p_275696_) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableDepthTest();
        var font = Minecraft.getInstance().font;
        var scale = this.height / 20.0F;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        p_275468_.pushPose();
        p_275468_.scale(scale, scale, 0.0F);
        p_275468_.translate(this.getX(), this.getY(), 0.0F);
        blit(p_275468_, this.getX(), this.getY(), this.isFocused() ? 20.0F : 0.0F, this.selected ? 20.0F : 0.0F, 20, 20, 64, 64);
        p_275468_.popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.showLabel) {
            drawString(p_275468_, font, this.getMessage(), this.getX() + (int) (24 * scale), this.getY() + (this.height - 8) / 2, TEXT_COLOR | Mth.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
