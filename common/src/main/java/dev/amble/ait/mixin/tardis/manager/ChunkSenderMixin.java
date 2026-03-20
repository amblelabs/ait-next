package dev.amble.ait.mixin.tardis.manager;

import dev.amble.ait.api.tardis.ServerTardis;
import dev.amble.ait.api.tardis.TardisManager;
import dev.amble.ait.common.impl.tardis.ServerTardisManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerChunkSender.class)
public class ChunkSenderMixin {

    @Inject(method = "sendChunk", at = @At("TAIL"))
    private static void sendChunk(ServerGamePacketListenerImpl packetListener, ServerLevel level, LevelChunk chunk, CallbackInfo ci) {
        ServerTardisManager manager = (ServerTardisManager) TardisManager.<ServerTardis>get(level);
        if (manager == null) return;

        manager.sendInChunk(packetListener.player, level, chunk);
    }
}
