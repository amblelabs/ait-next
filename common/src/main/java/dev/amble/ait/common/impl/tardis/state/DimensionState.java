package dev.amble.ait.common.impl.tardis.state;

import dev.amble.ait.api.AitAPI;
import dev.drtheo.ecs.state.NbtSerializer;
import dev.drtheo.ecs.state.TState;
import net.minecraft.nbt.CompoundTag;

public class DimensionState implements TState<DimensionState>, NbtSerializer {

    public static final Type<DimensionState> state = new NbtBacked<>(AitAPI.modLoc("dimension"), 0) {
        @Override
        public DimensionState fromNbt(CompoundTag nbt, boolean isClient) {
            return new DimensionState();
        }
    };

    @Override
    public Type<DimensionState> type() {
        return state;
    }

    @Override
    public void toNbt(CompoundTag nbt, boolean isClient) { }
}
