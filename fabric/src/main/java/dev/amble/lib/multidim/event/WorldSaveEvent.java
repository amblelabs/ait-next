package dev.amble.lib.multidim.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;

public class WorldSaveEvent {

    public static final Event<Save> EVENT = EventFactory.createArrayBacked(Save.class, callbacks -> world -> {
        for (Save callback : callbacks) {
            callback.onWorldSave(world);
        }
    });

    @FunctionalInterface
    public interface Save {
        void onWorldSave(ServerLevel world);
    }
}

