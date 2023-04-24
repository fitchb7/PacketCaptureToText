package com.hamusuke.packetcap.mixin;

import com.hamusuke.packetcap.PacketCapture;
import com.hamusuke.packetcap.packet.DedicatedServerPacketDetails;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.protocol.PacketFlow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PacketDecoder.class)
public class PacketDecoderMixin {
    @Shadow
    @Final
    private PacketFlow flow;

    @Inject(method = "decode", at = @At("HEAD"))
    private void decode(ChannelHandlerContext context, ByteBuf buf, List<Object> out, CallbackInfo ci) {
        var capture = PacketCapture.getInstance();
        if (capture.isCapturing()) {
            var byteBuf = buf.copy();
            int i = byteBuf.readableBytes();
            if (i != 0) {
                var friendlybytebuf = new FriendlyByteBuf(byteBuf);
                int j = friendlybytebuf.readVarInt();
                var packet = context.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get().createPacket(this.flow, j, friendlybytebuf);
                if (packet != null) {
                    if (friendlybytebuf.readableBytes() <= 0) {
                        capture.addToReceived(new DedicatedServerPacketDetails(packet, buf.copy()));
                    }
                }
            }
        }
    }
}
