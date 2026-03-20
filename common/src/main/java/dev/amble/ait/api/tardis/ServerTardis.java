package dev.amble.ait.api.tardis;

import dev.amble.ait.api.tardis.event.init.TardisLifecycleEvents;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class ServerTardis extends Tardis {

    public ServerTardis(UUID id) {
        super(id);
        TardisLifecycleEvents.handleCreated(this);
    }

    public ServerTardis(UUID id, CompoundTag nbt, boolean fix) {
        super(id, nbt, false, fix);
    }

    public void remove() {
        TardisLifecycleEvents.handleRemoved(this);
        this.clearStates();
    }

    public static ServerTardis fromNbt(CompoundTag nbt) {
        UUID id = nbt.getUUID(ID_TAG);
        return new ServerTardis(id, nbt, true);
    }
}