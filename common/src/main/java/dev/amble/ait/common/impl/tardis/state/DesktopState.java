package dev.amble.ait.common.impl.tardis.state;

import dev.amble.ait.api.AitAPI;
import dev.drtheo.ecs.state.NbtSerializer;
import dev.drtheo.ecs.state.TState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

public class DesktopState implements TState<DesktopState>, NbtSerializer {

    public @Nullable BlockPos doorPos; // door position is null so that we can test if the door is there or not. - Loqor
    public byte doorRot;

    public DesktopState() {
        this.doorRot = 0;
        this.doorPos = null;
    }

    public static final Type<DesktopState> state = new NbtBacked<>(AitAPI.modLoc("desktop"), 0) {
        @Override
        public DesktopState fromNbt(CompoundTag nbt, boolean isClient) {
            DesktopState desktopState = new DesktopState();
            if (nbt.contains("door_position")) {
                NbtUtils.readBlockPos(nbt, "door_position").ifPresent(pos -> desktopState.doorPos = pos);
            }
            desktopState.doorRot = nbt.getByte("door_rotation");
            return desktopState;
        }
    };

    @Override
    public void toNbt(CompoundTag nbt, boolean isClient) {
        if (doorPos != null) {
            nbt.put("door_position", NbtUtils.writeBlockPos(doorPos));
        }
        nbt.putByte("door_rotation", doorRot);
    }

    @Override
    public Type<DesktopState> type() {
        return state;
    }

    public void updateDoorPos(BlockPos pos) {
        updateDoorPos(pos, this.doorRot);
    }

    public void updateDoorPos(byte rot) {
        updateDoorPos(this.doorPos, rot);
    }

    public void updateDoorPos(BlockPos pos, byte rot) {
        this.doorPos = pos;
        this.doorRot = rot;
    }
}
