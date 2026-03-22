package dev.amble.ait.api.tardis.event.desktop;

import dev.amble.ait.api.tardis.ServerTardis;
import dev.amble.ait.api.tardis.event.NotifyEvent;
import dev.drtheo.ecs.event.TEvents;
import net.minecraft.server.MinecraftServer;

public interface DesktopPlacementEvents extends TEvents {

    static void handleBeforePlaced(ServerTardis tardis, MinecraftServer server) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.startDesktopPlacing(tardis, server)));
    }

    static void handlePlacing(ServerTardis tardis, MinecraftServer server) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.duringDesktopPlacement(tardis, server)));
    }

    static void handlePlaced(ServerTardis tardis, MinecraftServer server) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.endDesktopPlaced(tardis, server)));
    }

    Type<DesktopPlacementEvents> event = new Type<>(DesktopPlacementEvents.class);

    default void startDesktopPlacing(ServerTardis tardis, MinecraftServer server) { }
    default void duringDesktopPlacement(ServerTardis tardis, MinecraftServer server) { }
    default void endDesktopPlaced(ServerTardis tardis, MinecraftServer server) { }
}
