package dev.amble.ait.common.tardis;

import dev.amble.ait.common.lib.AitEcs;
import dev.amble.ait.common.tardis.event.init.TardisLifecycleEvents;
import dev.amble.ait.common.tardis.event.state.TardisStateEvents;
import dev.amble.ait.common.tardis.event.tick.TardisTickEvents;
import dev.drtheo.ecs.state.NbtSerializer;
import dev.drtheo.ecs.state.TState;
import dev.drtheo.ecs.state.TStateContainer;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Tardis extends TStateContainer.Delegate implements NbtSerializer {

    private static final String ID_TAG = "Id";

    protected final UUID id;

    protected final boolean isClient;
    protected boolean dirty;

    protected Tardis(UUID id) {
        super(AitEcs.States.createArrayHolder());

        this.id = id;
        this.isClient = false;
    }

    public static Tardis fromNbt(CompoundTag nbt, boolean isClient) {
        UUID id = nbt.getUUID(ID_TAG);
        return new Tardis(id, nbt, isClient);
    }

    public Tardis(UUID id, CompoundTag nbt, boolean isClient) {
        super(AitEcs.States.createArrayHolder());

        this.id = id;
        this.isClient = isClient;

        this.updateStates(nbt, isClient);

        TardisLifecycleEvents.handleLoaded(this);
    }

    public void tick() {
        TardisTickEvents.handleTick(this);
    }

    public boolean isClient() {
        return isClient;
    }

    //region State handling & serialization
    public boolean dirty() {
        return dirty;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void unmarkDirty() {
        this.dirty = false;
    }

    public void updateStates(CompoundTag nbt, boolean isClient) {
        CompoundTag states = nbt.getCompound("States");

        for (String key : states.getAllKeys()) {
            if (AitEcs.States.get(ResourceLocation.parse(key)) instanceof TState.NbtBacked<?> serializable) {
                Tag state = states.get(key);

                if (state instanceof CompoundTag compound) {
                    this.addState(serializable.decode(compound, isClient));
                } else {
                    this.removeState(serializable);
                }
            }
        }
    }

    @Override
    @Contract(mutates = "this")
    public boolean addState(@NotNull TState<?> state) {
        boolean result = super.addState(state);

        if (result)
            TardisStateEvents.handleAdd(this, state);

        return result;
    }

    @Override
    @Contract(mutates = "this")
    public <T extends TState<T>> @Nullable T removeState(@NotNull TState.Type<T> type) {
        T result = super.removeState(type);

        if (result != null)
            TardisStateEvents.handleRemove(this, result);

        return result;
    }

    @Contract(mutates = "this")
    public <T extends TState<T>> @Nullable T removeState(@NotNull T state) {
        return this.removeState(state.type());
    }

    @Override
    public void toNbt(@NotNull CompoundTag nbt, boolean isClient) {
        nbt.putUUID(ID_TAG, this.id);

        CompoundTag states = new CompoundTag();
        this.forEachState((i, state) -> stateToNbt(states, i, state, isClient));

        nbt.put("States", states);
    }

    @SuppressWarnings("rawtypes")
    private <T extends TState<T>> void stateToNbt(CompoundTag nbt, int i, @Nullable TState<T> state, boolean isClient) {
        if (state == null) {
            // do the diffing only if we're serializing for client
            if (isClient) nbt.put(AitEcs.States.get(i).id().toString(), ByteTag.ZERO);

            return;
        }

        TState.Type<T> type = state.type();

        if (!(type instanceof TState.NbtBacked backed))
            return;

        //noinspection unchecked
        nbt.put(type.id().toString(), backed.encode(state, isClient));
    }
    //endregion


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof Tardis tardis)
            return this.id.equals(tardis.id);

        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}