package com.hamusuke.packetcap.gui.screen;

import com.hamusuke.packetcap.PacketCapture;
import com.hamusuke.packetcap.filter.FilterType;
import com.hamusuke.packetcap.filter.PacketFilter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class AddPacketFilterScreen extends Screen {
    public static final Component TITLE = Component.translatable(PacketCapture.MOD_ID + ".add_filter").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    private static final Component FILTERED_BY = Component.translatable(PacketCapture.MOD_ID + ".filtered_by").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    private final Supplier<Component> translatableFactory = () -> Component.translatable(PacketCapture.MOD_ID + ".type", this.curType.toString()).withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    @Nullable
    private Screen parent;
    private EditBox filteredBy;
    private FilterType curType = FilterType.EQUALS;

    public AddPacketFilterScreen() {
        super(TITLE);
    }

    public AddPacketFilterScreen setParent(@Nullable Screen parent) {
        this.parent = parent;
        return this;
    }

    @Override
    protected void init() {
        super.init();

        this.filteredBy = this.addRenderableWidget(new EditBox(this.font, this.width / 4, this.height / 2 - 31, this.width / 2, 20, this.filteredBy, FILTERED_BY));
        this.filteredBy.setFormatter((s, integer) -> FormattedCharSequence.forward(s, Style.EMPTY.withFont(PacketCapture.MONO_FONT)));
        this.addRenderableWidget(Button.builder(this.translatableFactory.get(), p_93751_ -> {
            this.curType = this.curType.next();
            p_93751_.setMessage(this.translatableFactory.get());
        }).bounds(this.width / 4, this.height / 2 - 10, this.width / 2, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, p_93751_ -> {
            var v = this.filteredBy.getValue();
            if (StringUtils.isEmpty(v) || StringUtils.isBlank(v)) {
                return;
            }

            p_93751_.active = false;
            PacketCapture.getInstance().addFilter(new PacketFilter(v, this.curType));
            this.onClose();
        }).bounds(this.width / 4, this.height / 2 + 10, this.width / 2, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, p_93751_ -> this.onClose()).bounds(this.width / 4, this.height - 20, this.width / 2, 20).build());
    }

    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        this.renderBackground(p_96562_);
        drawCenteredString(p_96562_, this.font, this.getTitle(), this.width / 2, 20, 16777215);
        drawCenteredString(p_96562_, this.font, FILTERED_BY, this.width / 2, this.filteredBy.getY() - 13, 16777215);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
