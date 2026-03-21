package dev.amble.ait.common.impl.tardis.state;

import dev.amble.ait.api.AitAPI;
import dev.drtheo.ecs.state.NbtSerializer;
import dev.drtheo.ecs.state.TState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;

public class DimensionState implements TState<DimensionState>, NbtSerializer {

    public static final Type<DimensionState> state = new NbtBacked<>(AitAPI.modLoc("dimension"), 0) {
        @Override
        public DimensionState fromNbt(CompoundTag nbt, boolean isClient) {
            return new DimensionState();
        }

        @Override
        public @Nullable CompoundTag encode(DimensionState state, boolean isClient) {
            // don't sync.
            if (isClient) return null;

            return super.encode(state, false);
        }
    };

    @SuppressWarnings("NotNullFieldNotInitialized") // we initialize it later in #onPostLoaded and #onLoaded
    public WeakReference<ServerLevel> level;

    @Override
    public Type<DimensionState> type() {
        return state;
    }

    @Override
    public void toNbt(CompoundTag nbt, boolean isClient) { }
}
