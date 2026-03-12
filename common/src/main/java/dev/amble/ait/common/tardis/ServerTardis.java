package dev.amble.ait.common.tardis;

import dev.amble.ait.common.tardis.event.init.TardisLifecycleEvents;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class ServerTardis extends Tardis {

    public ServerTardis(UUID id) {
        super(id);
        TardisLifecycleEvents.handleCreated(this);
    }

    public ServerTardis(UUID id, CompoundTag nbt) {
        super(id, nbt, false);
    }

    public void remove() {
        TardisLifecycleEvents.handleRemoved(this);
        this.clearStates();
    }
}