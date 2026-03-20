package dev.amble.ait.common.lib;

import dev.amble.ait.api.tardis.event.block.ExteriorInteractionEvents;
import dev.amble.ait.api.tardis.event.init.TardisLifecycleEvents;
import dev.amble.ait.api.tardis.event.state.TardisStateEvents;
import dev.amble.ait.api.tardis.event.tick.TardisTickEvents;
import dev.amble.ait.common.impl.tardis.behavior.CapsuleExteriorBehavior;
import dev.amble.ait.common.impl.tardis.state.DoorState;
import dev.amble.ait.xplat.IXplatAbstractions;
import dev.drtheo.ecs.behavior.TBehaviorRegistry;
import dev.drtheo.ecs.event.TEventsRegistry;
import dev.drtheo.ecs.state.TAbstractStateRegistry;

public class AitEcs {

    public static final TAbstractStateRegistry States = new TAbstractStateRegistry() { };

    public static void init() {
        TAbstractStateRegistry.debug = IXplatAbstractions.INSTANCE.isDev();
    }

    public static void registerAll() {
        initState();
        initEvents();
        initBehavior();
    }

    public static void initState() {
        States.register(DoorState.state);
        States.freeze();
    }

    public static void initEvents() {
        TEventsRegistry.register(TardisLifecycleEvents.event);
        TEventsRegistry.register(TardisStateEvents.event);
        TEventsRegistry.register(TardisTickEvents.event);

        TEventsRegistry.register(ExteriorInteractionEvents.event);
        TEventsRegistry.freeze();
    }

    public static void initBehavior() {
        TBehaviorRegistry.register(CapsuleExteriorBehavior::new);
        TBehaviorRegistry.freeze();
    }
}
