package dev.amble.ait.common.impl.tardis.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.amble.ait.api.AitAPI;
import dev.drtheo.ecs.state.NbtSerializer;
import dev.drtheo.ecs.state.TState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ExteriorState implements TState<ExteriorState>, NbtSerializer {

    @SuppressWarnings("NotNullFieldNotInitialized") // lel
    public GlobalPos exteriorPos;
    public byte exteriorRot; // GlobalPos doesn't store the direction, so we can use a separate value. - Loqor

    public static final Type<ExteriorState> state = new NbtBacked<>(AitAPI.modLoc("exterior"), 0) {
        @Override
        public ExteriorState fromNbt(CompoundTag nbt, boolean isClient) {
            ExteriorState exteriorState = new ExteriorState();
            Tag savedTag = nbt.get("dimension");
            Codec<ResourceKey<Level>> keyCodec = ResourceKey.codec(Registries.DIMENSION);
            DataResult<ResourceKey<Level>> dimensionResult = keyCodec.parse(NbtOps.INSTANCE, savedTag);
            BlockPos blockPos = NbtUtils.readBlockPos(nbt, "position").orElse(new BlockPos(0, 64, 0));
            exteriorState.exteriorPos = GlobalPos.of(dimensionResult.getOrThrow(), blockPos);
            exteriorState.exteriorRot = nbt.getByte("rotation");

            return exteriorState;
        }
    };

    @Override
    public Type<ExteriorState> type() {
        return state;
    }

    @Override
    public void toNbt(@NotNull CompoundTag nbt, boolean isClient) {
        nbt.putString("dimension", exteriorPos.dimension().toString());
        nbt.put("position", NbtUtils.writeBlockPos(exteriorPos.pos()));
        nbt.putByte("rotation", exteriorRot);
    }
}
