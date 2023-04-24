package com.hamusuke.packetcap.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class TextButton extends Button {
    private final Font font;
    private final FormattedCharSequence message;

    public TextButton(Font font, int x, int y, FormattedCharSequence message, Button.OnPress onPress) {
        super(x, y, 0, 0, Component.empty(), onPress, DEFAULT_NARRATION);
        this.font = font;
        this.message = message;
        this.setWidth(this.font.width(this.message));
        this.setHeight(this.font.lineHeight);
    }

    public void renderWidget(PoseStack p_268041_, int p_268275_, int p_268109_, float p_268258_) {
        var color = this.isHoveredOrFocused() ? 5592575 : 16777215;
        drawString(p_268041_, this.font, this.message, this.getX(), this.getY(), color | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}
