package dev.amble.ait.fabric.multidim;

import dev.amble.lib.multidim.MultiDim;
import dev.amble.lib.multidim.MultiDimFileManager;
import dev.amble.lib.multidim.event.ServerCrashEvent;
import dev.amble.lib.multidim.event.WorldSaveEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public final class FabricMultiDimBootstrap {

    private static boolean initialized;

    private FabricMultiDimBootstrap() {
    }

    public static void init() {
        if (initialized) {
            return;
        }

        MultiDim.init();
        MultiDimFileManager.init();

        MultiDim.setWorldCallbacks(
                (server, world) -> ServerWorldEvents.LOAD.invoker().onWorldLoad(server, world),
                (server, world) -> ServerWorldEvents.UNLOAD.invoker().onWorldUnload(server, world)
        );

        ServerTickEvents.START_SERVER_TICK.register(server -> MultiDim.get(server).tick());
        ServerLifecycleEvents.SERVER_STARTED.register(MultiDimFileManager::readAll);

        ServerCrashEvent.EVENT.register((server, report) -> {
            for (var world : server.getAllLevels()) {
                MultiDimFileManager.writeIfNeeded(server, world);
            }
        });

        WorldSaveEvent.EVENT.register(world -> MultiDimFileManager.writeIfNeeded(world.getServer(), world));
        initialized = true;
    }
}

