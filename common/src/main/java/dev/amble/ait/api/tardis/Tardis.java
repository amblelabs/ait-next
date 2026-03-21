package dev.amble.ait.api.tardis;

import dev.amble.ait.common.lib.AitEcs;
import dev.amble.ait.api.tardis.event.state.TardisStateEvents;
import dev.amble.ait.api.tardis.event.tick.TardisTickEvents;
import dev.drtheo.ecs.state.NbtSerializer;
import dev.drtheo.ecs.state.TState;
import dev.drtheo.ecs.state.TStateContainer;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class Tardis extends TStateContainer.Delegate implements NbtSerializer {

    public static final String ID_TAG = "Id";
    public static final String STATES_TAG = "States";

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
        return new Tardis(id, nbt, isClient, false);
    }

    public Tardis(UUID id, CompoundTag nbt, boolean isClient, boolean fix) {
        super(AitEcs.States.createArrayHolder());

        this.id = id;
        this.isClient = isClient;

        this.updateStates(nbt, isClient, fix);
    }

    public void tick() {
        TardisTickEvents.handleTick(this);
    }

    public UUID id() {
        return id;
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

    public void updateStates(CompoundTag nbt, boolean isClient, boolean fix) {
        CompoundTag states = nbt.getCompound(STATES_TAG);

        for (String key : states.getAllKeys()) {
            if (AitEcs.States.get(ResourceLocation.parse(key)) instanceof TState.NbtBacked<?> serializable) {
                Tag state = states.get(key);

                if (state instanceof CompoundTag compound) {
                    this.addState(serializable.decode(fix ? serializable.update(compound, 0) : compound, isClient));
                } else {
                    this.removeState(serializable);
                }
            }
        }
    }

    @Override
    @Contract(mutates = "this")
    public boolean addState(TState<?> state) {
        boolean result = super.addState(state);

        if (result)
            TardisStateEvents.handleAdd(this, state);

        return result;
    }

    @Override
    @Contract(mutates = "this")
    public <T extends TState<T>> @Nullable T removeState(TState.Type<T> type) {
        T result = super.removeState(type);

        if (result != null)
            TardisStateEvents.handleRemove(this, result);

        return result;
    }

    @Contract(mutates = "this")
    public <T extends TState<T>> @Nullable T removeState(T state) {
        return this.removeState(state.type());
    }

    @Override
    public void toNbt(CompoundTag nbt, boolean isClient) {
        nbt.putUUID(ID_TAG, this.id);

        CompoundTag states = new CompoundTag();

        // FIXME: this only works once. by this i mean diffing.
        this.forEachState((i, state) -> stateToNbt(states, i, state, isClient));

        nbt.put(STATES_TAG, states);
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
        CompoundTag tag = backed.encode(state, isClient);
        if (tag == null) return;

        nbt.put(type.id().toString(), tag);
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