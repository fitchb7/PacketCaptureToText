package com.hamusuke.packetcap.gui.overlay;

import com.hamusuke.packetcap.PacketCapture;
import com.hamusuke.packetcap.utils.ByteConversion;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PacketCaptureOverlay extends GuiComponent {
    private final Minecraft mc;
    private final PacketCapture capture;

    public PacketCaptureOverlay(Minecraft mc, PacketCapture capture) {
        this.mc = mc;
        this.capture = capture;
    }

    public void render(PoseStack stack) {
        this.drawSentPackets(stack);
        this.drawReceivedPackets(stack);
    }

    private void drawSentPackets(PoseStack stack) {
        var list = this.capture.getSentPackets();
        int size = list.size();
        long sent = this.capture.getSentPacketNum();
        long bytes = this.capture.getSentBytes();
        var component = this.mc.isLocalServer() ? Component.translatable(PacketCapture.MOD_ID + ".sent.detail", sent) : Component.translatable(PacketCapture.MOD_ID + ".sent.detail.dedicated", sent, bytes, ByteConversion.convertBytes(bytes));
        component.withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
        fill(stack, 1, 2 - 1, 2 + this.mc.font.width(component) + 1, 2 + 9 - 1, -1873784752);
        this.mc.font.draw(stack, component, 2.0F, 2.0F, 14737632);

        if (size > 0) {
            for (int i = Mth.clamp(size - 1 - this.mc.getWindow().getGuiScaledHeight() / 9 - 1, 0, size - 1); i < size; ++i) {
                var s = Component.literal(list.get(i).getPacketClassName()).withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
                int k = this.mc.font.width(s);
                if (k > 0) {
                    int i1 = 2 + 9 + 9 * (size - 1 - i);
                    fill(stack, 1, i1 - 1, 2 + k + 1, i1 + 9 - 1, -1873784752);
                    this.mc.font.draw(stack, s, 2.0F, (float) i1, 14737632);
                }
            }
        }
    }

    private void drawReceivedPackets(PoseStack stack) {
        var list = this.capture.getReceivedPackets();
        int size = list.size();
        long received = this.capture.getReceivedPacketNum();
        long bytes = this.capture.getReceivedBytes();
        var component = this.mc.isLocalServer() ? Component.translatable(PacketCapture.MOD_ID + ".received.detail", received) : Component.translatable(PacketCapture.MOD_ID + ".received.detail.dedicated", received, bytes, ByteConversion.convertBytes(bytes));
        component.withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
        int k2 = this.mc.font.width(component);
        int l2 = this.mc.getWindow().getGuiScaledWidth() - 2 - k2;
        fill(stack, l2 - 1, 2 - 1, l2 + k2 + 1, 2 + 9 - 1, -1873784752);
        this.mc.font.draw(stack, component, (float) l2, 2.0F, 14737632);

        if (size > 0) {
            for (int i = Mth.clamp(size - 1 - this.mc.getWindow().getGuiScaledHeight() / 9 - 1, 0, size - 1); i < size; ++i) {
                var s = Component.literal(list.get(i).getPacketClassName()).withStyle(style -> style.withFont(PacketCapture.MONO_FONT));
                int k = this.mc.font.width(s);
                if (k > 0) {
                    int l = this.mc.getWindow().getGuiScaledWidth() - 2 - k;
                    int i1 = 2 + 9 + 9 * (size - 1 - i);
                    fill(stack, l - 1, i1 - 1, l + k + 1, i1 + 9 - 1, -1873784752);
                    this.mc.font.draw(stack, s, (float) l, (float) i1, 14737632);
                }
            }
        }
    }
}
