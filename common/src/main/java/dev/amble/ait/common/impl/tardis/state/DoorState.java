package dev.amble.ait.common.impl.tardis.state;

import dev.amble.ait.api.AitAPI;
import dev.drtheo.ecs.state.NbtSerializer;
import dev.drtheo.ecs.state.TState;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class DoorState implements TState<DoorState>, NbtSerializer {

    // TODO: maybe use byte magic
    public boolean rightOpen;
    public boolean leftOpen;

    public static final Type<DoorState> state = new NbtBacked<>(AitAPI.modLoc("door"), 0) {
        @Override
        public DoorState fromNbt(@NotNull CompoundTag nbt, boolean isClient) {
            DoorState door = new DoorState();
            door.rightOpen = nbt.getBoolean("right");
            door.leftOpen = nbt.getBoolean("left");

            return door;
        }
    };

    public boolean closed() {
        return !rightOpen && !leftOpen;
    }

    public boolean anyOpen() {
        return rightOpen || leftOpen;
    }

    @Override
    public void toNbt(@NotNull CompoundTag nbt, boolean isClient) {
        nbt.putBoolean("right", rightOpen);
        nbt.putBoolean("left", leftOpen);
    }

    @Override
    public Type<DoorState> type() {
        return state;
    }
}
