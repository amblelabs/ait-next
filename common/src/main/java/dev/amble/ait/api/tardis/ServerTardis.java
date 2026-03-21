package dev.amble.ait.api.tardis;

import dev.amble.ait.api.tardis.event.init.TardisLifecycleEvents;
import dev.drtheo.ecs.state.TState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class ServerTardis extends Tardis {

    public static ServerTardis create(ServerLevel serverLevel, TState<?>... states) {
        return create(serverLevel, UUID.randomUUID(), states);
    }

    // No code in this commit will be AI-generated. Nor any more code in the future. I'm sick of its usage and I've found my brain
    // turning to rot using it. All code from now on will be __explicitly__ HUMAN. MADE. - Loqor

    public static ServerTardis create(ServerLevel serverLevel, UUID uuid, TState<?>... states) {
        ServerTardis tardis = new ServerTardis(uuid);

        for (TState<?> state : states) {
            tardis.addState(state);
        }

        TardisManager.getOrCreate(serverLevel).add(tardis);
        TardisLifecycleEvents.handlePostCreated(tardis, serverLevel.getServer());

        return tardis;
    }

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