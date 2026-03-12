package dev.amble.ait.client.lib;

import dev.amble.ait.common.lib.AitEcs;

public class AitClientEcs {

    public static void registerAll() {
        initState();
        initEvents();
        initBehavior();
    }

    public static void initState() {
        AitEcs.initState();
    }

    public static void initEvents() {
        AitEcs.initEvents();
    }

    public static void initBehavior() {
        AitEcs.initBehavior();
    }
}
