package com.hamusuke.packetcap.gui.screen;

import com.hamusuke.packetcap.PacketCapture;
import com.hamusuke.packetcap.gui.components.ScalableCheckbox;
import com.hamusuke.packetcap.packet.DedicatedPacket;
import com.hamusuke.packetcap.packet.PacketDetails;
import com.hamusuke.packetcap.utils.ByteConversion;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PacketListScreen extends Screen {
    private static final Component TITLE = Component.translatable(PacketCapture.MOD_ID + ".packetListScreen.title").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    private static final Component START_CAPTURING = Component.translatable(PacketCapture.MOD_ID + ".start_cap").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    private static final Component STOP_CAPTURING = Component.translatable(PacketCapture.MOD_ID + ".stop_cap").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    private static final Component CLEAR = Component.translatable(PacketCapture.MOD_ID + ".clear").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    private static final Component SENT = Component.translatable(PacketCapture.MOD_ID + ".sent").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    private static final Component RECEIVED = Component.translatable(PacketCapture.MOD_ID + ".received").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    private static final Component AUTO_SCROLL = Component.translatable(PacketCapture.MOD_ID + ".auto_scroll").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    @Nullable
    private Screen parent;
    private final PacketCapture capture;
    private PacketList sentPackets;
    private PacketList receivedPackets;
    private ScalableCheckbox autoTxScroll;
    private ScalableCheckbox autoRxScroll;

    public PacketListScreen(PacketCapture capture) {
        super(TITLE);
        this.capture = capture;
    }

    public PacketListScreen setParent(@Nullable Screen parent) {
        this.parent = parent;
        return this;
    }

    @Override
    protected void init() {
        super.init();

        double d = this.sentPackets != null ? this.sentPackets.getScrollAmount() : 0.0D;
        this.sentPackets = new PacketList(this.capture.getSentPackets(), 30, PacketListType.SENT);
        if (this.autoTxScroll == null) {
            this.autoTxScroll = new ScalableCheckbox(this.sentPackets.getRight() - 20, 20, 10, 10, AUTO_SCROLL, true);
            this.autoTxScroll.setTooltip(Tooltip.create(AUTO_SCROLL));
        }
        this.sentPackets.setScrollAmount(this.autoTxScroll.selected() ? this.sentPackets.getMaxScroll() : d);
        this.addWidget(this.sentPackets);
        this.addRenderableWidget(this.autoTxScroll);

        double d1 = this.receivedPackets != null ? this.receivedPackets.getScrollAmount() : 0.0D;
        this.receivedPackets = new PacketList(this.capture.getReceivedPackets(), this.height / 2 + 10, PacketListType.RECEIVED);
        if (this.autoRxScroll == null) {
            this.autoRxScroll = new ScalableCheckbox(this.sentPackets.getRight() - 20, this.height / 2 + 10, 10, 10, AUTO_SCROLL, true);
            this.autoRxScroll.setTooltip(Tooltip.create(AUTO_SCROLL));
        }
        this.receivedPackets.setScrollAmount(this.autoRxScroll.selected() ? this.receivedPackets.getMaxScroll() : d1);
        this.addWidget(this.receivedPackets);
        this.addRenderableWidget(this.autoRxScroll);

        this.addRenderableWidget(Button.builder(this.capture.isCapturing() ? STOP_CAPTURING : START_CAPTURING, p_93751_ -> {
            this.capture.toggle();
            p_93751_.setMessage(this.capture.isCapturing() ? STOP_CAPTURING : START_CAPTURING);
        }).bounds(0, this.height - 20, this.width / 4, 20).build());

        this.addRenderableWidget(Button.builder(PacketFilterScreen.TITLE, p_93751_ -> this.minecraft.setScreen(new PacketFilterScreen().setParent(this))).bounds(this.width / 4, this.height - 20, this.width / 4, 20).build());

        this.addRenderableWidget(Button.builder(CLEAR, p_93751_ -> this.capture.clearPackets()).bounds(this.width / 2, this.height - 20, this.width / 4, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, p_93751_ -> this.onClose()).bounds(this.width * 3 / 4, this.height - 20, this.width / 4, 20).build());
    }

    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        this.receivedPackets.render(p_96562_, p_96563_, p_96564_, p_96565_);
        this.sentPackets.render(p_96562_, p_96563_, p_96564_, p_96565_);
        drawCenteredString(p_96562_, this.font, this.title, this.width / 2, 5, 16777215);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private enum PacketListType {
        SENT(PacketListScreen.SENT),
        RECEIVED(PacketListScreen.RECEIVED);

        private final Component text;

        PacketListType(Component text) {
            this.text = text;
        }
    }

    private final class PacketList extends ObjectSelectionList<PacketList.Entry> {
        private final PacketListType type;

        private PacketList(List<PacketDetails> list, int top, PacketListType type) {
            super(PacketListScreen.this.minecraft, PacketListScreen.this.width, PacketListScreen.this.height / 2 - 20, top, top + PacketListScreen.this.height / 2 - 30, 10);
            this.type = type;

            for (var details : list) {
                this.addEntry(new Entry(details));
            }
        }

        @Override
        protected void renderDecorations(PoseStack p_93443_, int p_93444_, int p_93445_) {
            var width = this.minecraft.font.width(this.type.text);
            var x = this.minecraft.font.drawShadow(p_93443_, this.type.text, (this.getRight() + this.getLeft()) / 2.0F - width / 2.0F, this.getTop() - 10, 16777215);
            x = Math.min(x, this.getRight() - 20);
            (switch (this.type) {
                case SENT -> PacketListScreen.this.autoTxScroll;
                case RECEIVED -> PacketListScreen.this.autoRxScroll;
            }).setPosition(x, this.getTop() - 10);
        }

        @Override
        public boolean mouseScrolled(double p_93416_, double p_93417_, double p_93418_) {
            var prev = this.getScrollAmount();
            var r = super.mouseScrolled(p_93416_, p_93417_, p_93418_);
            var cur = this.getScrollAmount();

            (switch (this.type) {
                case SENT -> PacketListScreen.this.autoTxScroll;
                case RECEIVED -> PacketListScreen.this.autoRxScroll;
            }).setSelected(cur == (double) this.getMaxScroll() && prev == cur);

            return r;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getRight() - 6;
        }

        private final class Entry extends ObjectSelectionList.Entry<Entry> {
            private static final Component TEXT = Component.translatable(PacketCapture.MOD_ID + ".button.show.details").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
            private final PacketDetails packetDetails;
            private final Button details;

            private Entry(PacketDetails details) {
                this.packetDetails = details;
                this.details = Button.builder(TEXT, p_93751_ -> {
                    PacketListScreen.this.minecraft.setScreen(new PacketDetailsScreen(PacketListScreen.this, this.packetDetails));
                }).bounds(0, 0, 50, 10).build();
            }

            @Override
            public Component getNarration() {
                return GameNarrator.NO_TITLE;
            }

            @Override
            public void render(PoseStack p_93523_, int p_93524_, int p_93525_, int p_93526_, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_) {
                var text = Component.literal(this.packetDetails.getPacketClassName() + (this.packetDetails instanceof DedicatedPacket dedicatedPacket ? " (" + ByteConversion.convertBytes(dedicatedPacket.getSize()) + ")" : ""));
                text.withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
                var width = PacketListScreen.this.font.width(text);
                var x = PacketListScreen.this.font.drawShadow(p_93523_, text, (float) (this.list.getRight() / 2 - (width + 50) / 2), p_93525_ + 1, 16777215);
                x = Math.min(x, this.list.getRight() - 50);
                this.details.setX(x);
                this.details.setY(p_93525_);
                this.details.render(p_93523_, p_93529_, p_93530_, p_93532_);
            }

            @Override
            public boolean mouseClicked(double p_94737_, double p_94738_, int p_94739_) {
                return this.details.mouseClicked(p_94737_, p_94738_, p_94739_);
            }
        }
    }
}
