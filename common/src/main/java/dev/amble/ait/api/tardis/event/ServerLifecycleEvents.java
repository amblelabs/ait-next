package dev.amble.ait.api.tardis.event;

import dev.drtheo.ecs.event.TEvents;
import net.minecraft.server.MinecraftServer;

public interface ServerLifecycleEvents extends TEvents {

    static void handleServerStarted(MinecraftServer server) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.onServerStarted(server)));
    }

    static void handleServerStopping(MinecraftServer server) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.onServerStopping(server)));
    }

    Type<ServerLifecycleEvents> event = new Type<>(ServerLifecycleEvents.class);

    void onServerStarted(MinecraftServer server);
    void onServerStopping(MinecraftServer server);
}
