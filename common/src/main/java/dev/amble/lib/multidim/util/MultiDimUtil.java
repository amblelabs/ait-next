package dev.amble.lib.multidim.util;

import dev.amble.lib.multidim.api.MutableRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;

public final class MultiDimUtil {

    private MultiDimUtil() {
    }

    public static MappedRegistry<LevelStem> getDimensionsRegistry(MinecraftServer server) {
        return (MappedRegistry<LevelStem>) server.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
    }

    @SuppressWarnings("unchecked")
    public static MutableRegistry<LevelStem> getMutableDimensionsRegistry(MinecraftServer server) {
        return (MutableRegistry<LevelStem>) getDimensionsRegistry(server);
    }
}


