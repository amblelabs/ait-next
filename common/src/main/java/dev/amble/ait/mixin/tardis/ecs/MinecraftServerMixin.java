package dev.amble.ait.mixin.tardis.ecs;

import dev.amble.ait.api.tardis.event.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "stopServer", at = @At("HEAD"))
    public void onStopping(CallbackInfo ci) {
        ServerLifecycleEvents.handleServerStopping((MinecraftServer) (Object) this);
    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;initServer()Z"))
    public void onStarted(CallbackInfo ci) {
        ServerLifecycleEvents.handleServerStarted((MinecraftServer) (Object) this);
    }
}
