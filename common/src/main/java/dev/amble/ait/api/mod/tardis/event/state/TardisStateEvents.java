package dev.amble.ait.api.mod.tardis.event.state;

import dev.amble.ait.api.mod.tardis.Tardis;
import dev.amble.ait.api.mod.tardis.event.TardisNotifyEvent;
import dev.drtheo.ecs.event.TEvents;
import dev.drtheo.ecs.state.TState;

public interface TardisStateEvents extends TEvents {

    static void handleAdd(Tardis tardis, TState<?> state) {
        TEvents.handle(new TardisNotifyEvent<>(event, tardis, (ev, t) -> ev.onStateAdded(t, state)));
    }

    static void handleRemove(Tardis tardis, TState<?> state) {
        TEvents.handle(new TardisNotifyEvent<>(event, tardis, (ev, t) -> ev.onStateRemoved(t, state)));
    }

    Type<TardisStateEvents> event = new Type<>(TardisStateEvents.class);

    void onStateAdded(Tardis tardis, TState<?> state);

    void onStateRemoved(Tardis tardis, TState<?> state);
}