package dev.amble.ait.common.lib;

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
        States.freeze();
    }

    public static void initEvents() {
        TEventsRegistry.freeze();
    }

    public static void initBehavior() {
        TBehaviorRegistry.freeze();
    }
}
