package dev.amble.ait.common.lib;

import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AitBlockSetTypes {
    public static void registerBlocks(Consumer<BlockSetType> r) {
        for (var type : TYPES) {
            r.accept(type);
        }
    }

    private static final List<BlockSetType> TYPES = new ArrayList<>();

    //

    private static BlockSetType register(BlockSetType type) {
        TYPES.add(type);
        return type;
    }
}