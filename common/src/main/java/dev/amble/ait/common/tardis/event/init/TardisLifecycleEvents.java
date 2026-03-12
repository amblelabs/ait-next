package dev.amble.ait.common.tardis.event.init;

import dev.amble.ait.common.tardis.ServerTardis;
import dev.amble.ait.common.tardis.Tardis;
import dev.amble.ait.common.tardis.event.TardisNotifyEvent;
import dev.drtheo.ecs.event.TEvents;

public interface TardisLifecycleEvents extends TEvents {

    static void handleCreated(ServerTardis tardis) {
        TEvents.handle(new TardisNotifyEvent<>(event, tardis, TardisLifecycleEvents::onCreated));
    }

    static void handleLoaded(Tardis tardis) {
        TEvents.handle(new TardisNotifyEvent<>(event, tardis, TardisLifecycleEvents::onLoaded));
    }

    static void handleRemoved(ServerTardis tardis) {
        TEvents.handle(new TardisNotifyEvent<>(event, tardis, TardisLifecycleEvents::onRemoved));
    }

    Type<TardisLifecycleEvents> event = new Type<>(TardisLifecycleEvents.class);

    default void onCreated(ServerTardis tardis) { }
    default void onLoaded(Tardis tardis) { }
    default void onRemoved(ServerTardis tardis) { }
}