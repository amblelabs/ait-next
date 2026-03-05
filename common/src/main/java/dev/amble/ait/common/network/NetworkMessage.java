package dev.amble.ait.common.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

// https://github.com/VazkiiMods/Botania/blob/1.18.x/Common/src/main/java/vazkii/botania/network/IPacket.java
public interface NetworkMessage {

    default FriendlyByteBuf toBuf() {
        var ret = new FriendlyByteBuf(Unpooled.buffer());
        serialize(ret);
        return ret;
    }

    void serialize(FriendlyByteBuf buf);

    ResourceLocation getFabricId();
}