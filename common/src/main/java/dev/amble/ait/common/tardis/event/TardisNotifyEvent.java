package dev.amble.ait.common.tardis.event;

import dev.amble.ait.common.tardis.Tardis;
import dev.drtheo.ecs.event.TEvent;
import dev.drtheo.ecs.event.TEvents;
import dev.drtheo.ecs.state.StateResolveError;

import java.util.function.BiConsumer;

public record TardisNotifyEvent<T extends TEvents, S extends Tardis>(TEvents.BaseType<T> type, S stargate, BiConsumer<T, S> func) implements TEvent.Notify<T> {

    @Override
    public void handle(T t) throws StateResolveError {
        func.accept(t, stargate);
    }
}