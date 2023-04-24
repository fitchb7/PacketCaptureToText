package com.hamusuke.packetcap.gui.screen;

import com.hamusuke.packetcap.PacketCapture;
import com.hamusuke.packetcap.clazz.field.MapField;
import com.hamusuke.packetcap.clazz.visitor.ClassVisitor;
import com.hamusuke.packetcap.clazz.visitor.MapVisitor;
import com.hamusuke.packetcap.gui.components.ClassFieldList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class VisitClassScreen extends Screen {
    private static final Component BACK_TO_DETAILS = Component.translatable(PacketCapture.MOD_ID + ".back_to_details").withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
    private final PacketDetailsScreen packetDetailsScreen;
    @Nullable
    private final Screen parent;
    private final ClassVisitor visitor;
    private Fields list;

    public VisitClassScreen(PacketDetailsScreen screen, @Nullable Screen parent, ClassVisitor visitor) {
        super(Component.literal(visitor.getFullClassName()).withStyle(style -> style.withFont(PacketCapture.MONO_FONT)));
        this.packetDetailsScreen = screen;
        this.parent = parent;
        this.visitor = visitor;
        this.visitor.visit();
    }

    @Override
    protected void init() {
        super.init();

        this.list = new Fields();
        this.addWidget(this.list);

        this.addRenderableWidget(Button.builder(BACK_TO_DETAILS, p_93751_ -> this.minecraft.setScreen(this.packetDetailsScreen)).bounds(0, this.height - 20, this.width / 2, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, p_93751_ -> this.onClose()).bounds(this.width / 2, this.height - 20, this.width / 2, 20).build());
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

    private final class Fields extends ClassFieldList {
        public Fields() {
            super(VisitClassScreen.this.minecraft, VisitClassScreen.this.width, VisitClassScreen.this.height - 20, 20, VisitClassScreen.this.height - 20, 10, VisitClassScreen.this.visitor, VisitClassScreen.this.packetDetailsScreen, VisitClassScreen.this);

            var visitor = VisitClassScreen.this.visitor;
            var map = visitor instanceof MapVisitor;

            if (map) {
                this.addEntry(new TextEntry(Component.literal("[").withStyle(style -> style.withFont(PacketCapture.MONO_FONT)).getVisualOrderText()));
            }

            var mapFields = visitor.getFields().stream().filter(classField -> classField instanceof MapField).map(classField -> (MapField) classField).toList();
            for (int i = 0; i < mapFields.size(); i++) {
                var mapField = mapFields.get(i);
                var last = i >= mapFields.size() - 1;
                var key = mapField.getKeyVisitor();
                var value = mapField.getVisitor();

                this.addEntry(new TextEntry(Component.literal("{").withStyle(style -> style.withFont(PacketCapture.MONO_FONT)).getVisualOrderText()));
                this.addEntry(key, key.toString(), VisitClassScreen.this.packetDetailsScreen, VisitClassScreen.this);
                this.addEntry(new TextEntry(Component.literal("").getVisualOrderText()));
                this.addEntry(new TextEntry(Component.literal("->").withStyle(style -> style.withFont(PacketCapture.MONO_FONT)).getVisualOrderText()));
                this.addEntry(new TextEntry(Component.literal("").getVisualOrderText()));
                this.addEntry(value, value.toString(), VisitClassScreen.this.packetDetailsScreen, VisitClassScreen.this);
                this.addEntry(new TextEntry(Component.literal("}" + (last ? "" : ",")).withStyle(style -> style.withFont(PacketCapture.MONO_FONT)).getVisualOrderText()));
                this.addEntry(new TextEntry(Component.literal("").getVisualOrderText()));
                this.addEntry(new TextEntry(Component.literal("").getVisualOrderText()));
            }

            if (map) {
                this.addEntry(new TextEntry(Component.literal("]").withStyle(style -> style.withFont(PacketCapture.MONO_FONT)).getVisualOrderText()));
            }
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width - 6;
        }
    }
}
