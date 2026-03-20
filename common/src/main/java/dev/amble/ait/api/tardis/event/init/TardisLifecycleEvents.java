package dev.amble.ait.api.tardis.event.init;

import dev.amble.ait.api.tardis.ServerTardis;
import dev.amble.ait.api.tardis.Tardis;
import dev.amble.ait.api.tardis.event.NotifyEvent;
import dev.drtheo.ecs.event.TEvents;
import net.minecraft.server.MinecraftServer;

public interface TardisLifecycleEvents extends TEvents {

    static void handleCreated(ServerTardis tardis) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.onCreated(tardis)));
    }

    static void handlePostCreated(ServerTardis tardis, MinecraftServer server) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.onPostCreated(tardis, server)));
    }

    static void handleLoaded(Tardis tardis) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.onLoaded(tardis)));
    }

    static void handleRemoved(ServerTardis tardis) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.onRemoved(tardis)));
    }

    Type<TardisLifecycleEvents> event = new Type<>(TardisLifecycleEvents.class);

    default void onCreated(ServerTardis tardis) { }
    default void onPostCreated(ServerTardis tardis, MinecraftServer server) { }
    default void onLoaded(Tardis tardis) { }
    default void onRemoved(ServerTardis tardis) { }
}