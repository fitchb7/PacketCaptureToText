package com.hamusuke.packetcap.gui.screen;

import com.hamusuke.packetcap.PacketCapture;
import com.hamusuke.packetcap.filter.PacketFilter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Collection;

public class PacketFilterScreen extends Screen {
    public static final Component TITLE = Component.translatable(PacketCapture.MOD_ID + ".packet_filter").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    private static final Component RELOAD = Component.translatable(PacketCapture.MOD_ID + ".reload").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    private static final Component REMOVE_ALL = Component.translatable(PacketCapture.MOD_ID + ".remove_all").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    @Nullable
    private Screen parent;
    private PacketFilterList list;

    public PacketFilterScreen() {
        super(TITLE);
    }

    public PacketFilterScreen setParent(@Nullable Screen parent) {
        this.parent = parent;
        return this;
    }

    @Override
    protected void init() {
        super.init();

        this.list = new PacketFilterList(PacketCapture.getInstance().getFilters());
        this.addWidget(this.list);

        this.addRenderableWidget(Button.builder(REMOVE_ALL, p_93751_ -> {
            PacketCapture.getInstance().removeAllFilters();
            this.init();
        }).bounds(0, this.height - 20, this.width / 4, 20).build());
        this.addRenderableWidget(Button.builder(RELOAD, p_93751_ -> {
            PacketCapture.getInstance().loadFilters();
            this.init();
        }).bounds(this.width / 4, this.height - 20, this.width / 4, 20).build());
        this.addRenderableWidget(Button.builder(AddPacketFilterScreen.TITLE, p_93751_ -> this.minecraft.setScreen(new AddPacketFilterScreen().setParent(this))).bounds(this.width / 2, this.height - 20, this.width / 4, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, p_93751_ -> this.onClose()).bounds(this.width * 3 / 4, this.height - 20, this.width / 4, 20).build());
    }

    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        this.list.render(p_96562_, p_96563_, p_96564_, p_96565_);
        drawCenteredString(p_96562_, this.font, this.title, this.width / 2, 5, 16777215);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private final class PacketFilterList extends ObjectSelectionList<PacketFilterList.Entry> {
        private static final Component LIST_TITLE = Component.translatable(PacketCapture.MOD_ID + ".cur_filtering").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));

        private PacketFilterList(Collection<PacketFilter> list) {
            super(PacketFilterScreen.this.minecraft, PacketFilterScreen.this.width, PacketFilterScreen.this.height - 20, 30, PacketFilterScreen.this.height - 20, 10);

            for (var filter : list) {
                this.addEntry(new Entry(filter));
            }
        }

        @Override
        protected void renderDecorations(PoseStack p_93443_, int p_93444_, int p_93445_) {
            drawCenteredString(p_93443_, this.minecraft.font, LIST_TITLE, (this.getRight() + this.getLeft()) / 2, this.getTop() - 10, 16777215);
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getRight() - 6;
        }

        private final class Entry extends ObjectSelectionList.Entry<Entry> {
            private static final Component TEXT = Component.translatable(PacketCapture.MOD_ID + ".button.remove").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
            private final PacketFilter packetFilter;
            private final Button remove;

            private Entry(PacketFilter filter) {
                this.packetFilter = filter;
                this.remove = Button.builder(TEXT, p_93751_ -> {
                    p_93751_.active = false;
                    PacketFilterList.this.removeEntry(this);
                    PacketCapture.getInstance().removeFilter(this.packetFilter);
                }).bounds(0, 0, 50, 10).build();
            }

            @Override
            public Component getNarration() {
                return GameNarrator.NO_TITLE;
            }

            @Override
            public void render(PoseStack p_93523_, int p_93524_, int p_93525_, int p_93526_, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_) {
                var text = Component.translatable(PacketCapture.MOD_ID + ".filter_detail", this.packetFilter.filteredBy(), this.packetFilter.filterType().toString());
                text.withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
                var fontWidth = PacketFilterScreen.this.font.width(text);
                var x = PacketFilterScreen.this.font.drawShadow(p_93523_, text, (float) (this.list.getRight() / 2 - (fontWidth + 50) / 2), p_93525_ + 1, 16777215);
                x = Math.min(x, this.list.getRight() - 50);
                this.remove.setX(x);
                this.remove.setY(p_93525_);
                this.remove.render(p_93523_, p_93529_, p_93530_, p_93532_);
            }

            @Override
            public boolean mouseClicked(double p_94737_, double p_94738_, int p_94739_) {
                return this.remove.mouseClicked(p_94737_, p_94738_, p_94739_);
            }
        }
    }
}
