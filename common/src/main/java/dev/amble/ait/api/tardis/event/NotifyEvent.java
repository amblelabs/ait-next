package dev.amble.ait.api.tardis.event;

import dev.drtheo.ecs.event.TEvent;
import dev.drtheo.ecs.event.TEvents;
import dev.drtheo.ecs.state.StateResolveError;

import java.util.function.Consumer;

public record NotifyEvent<T extends TEvents>(TEvents.BaseType<T> type, Consumer<T> consumer) implements TEvent.Notify<T> {

    @Override
    public void handle(T handler) throws StateResolveError {
        consumer.accept(handler);
    }

    @Override
    public TEvents.BaseType<T> type() {
        return type;
    }
}
