package dev.amble.ait.common.tardis.event.tick;

import dev.amble.ait.common.tardis.Tardis;
import dev.amble.ait.common.tardis.event.TardisNotifyEvent;
import dev.drtheo.ecs.event.TEvents;

public interface TardisTickEvents extends TEvents {

    static void handleTick(Tardis tardis) {
        TEvents.handle(new TardisNotifyEvent<>(event, tardis, TardisTickEvents::tick));
    }

    Type<TardisTickEvents> event = new Type<>(TardisTickEvents.class);

    void tick(Tardis tardis);
}