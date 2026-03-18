package dev.amble.ait.common.lib;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.blocks.ConsoleBlockEntity;
import dev.amble.ait.common.blocks.DoorBlockEntity;
import dev.amble.ait.common.blocks.ExteriorBlockEntity;
import dev.amble.ait.xplat.IXplatAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class AitBlockEntities {
    public static void registerTiles(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
        for (var e : BLOCK_ENTITIES.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    private static final Map<ResourceLocation, BlockEntityType<?>> BLOCK_ENTITIES = new LinkedHashMap<>();

    //

    public static final BlockEntityType<ExteriorBlockEntity> EXTERIOR_BLOCK_ENTITY =
            register("exterior", ExteriorBlockEntity::new, AitBlocks.EXTERIOR_BLOCK);

    public static final BlockEntityType<DoorBlockEntity> DOOR_BLOCK_ENTITY =
            register("tardis_door", DoorBlockEntity::new, AitBlocks.TARDIS_DOOR);

    public static final BlockEntityType<ConsoleBlockEntity> CONSOLE_BLOCK_ENTITY =
            register("console", ConsoleBlockEntity::new, AitBlocks.CONSOLE);

    private static <T extends BlockEntity> BlockEntityType<T> register(String id,
        BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
        var ret = IXplatAbstractions.INSTANCE.createBlockEntityType(func, blocks);

        var old = BLOCK_ENTITIES.put(AitAPI.modLoc(id), ret);
        if (old != null) {
            throw new IllegalArgumentException("Duplicate id " + id);
        }
        return ret;
    }
}