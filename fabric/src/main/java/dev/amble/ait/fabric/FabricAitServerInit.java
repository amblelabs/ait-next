package dev.amble.ait.fabric;

import dev.amble.ait.common.lib.AitEcs;
import net.fabricmc.api.DedicatedServerModInitializer;

public class FabricAitServerInit implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        AitEcs.registerAll();
    }
}
