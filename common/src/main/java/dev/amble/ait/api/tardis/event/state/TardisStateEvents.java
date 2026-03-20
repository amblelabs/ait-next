package dev.amble.ait.api.tardis.event.state;

import dev.amble.ait.api.tardis.Tardis;
import dev.amble.ait.api.tardis.event.NotifyEvent;
import dev.drtheo.ecs.event.TEvents;
import dev.drtheo.ecs.state.TState;

public interface TardisStateEvents extends TEvents {

    static void handleAdd(Tardis tardis, TState<?> state) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.onStateAdded(tardis, state)));
    }

    static void handleRemove(Tardis tardis, TState<?> state) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.onStateRemoved(tardis, state)));
    }

    Type<TardisStateEvents> event = new Type<>(TardisStateEvents.class);

    void onStateAdded(Tardis tardis, TState<?> state);

    void onStateRemoved(Tardis tardis, TState<?> state);
}