package dev.drtheo.multidim.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jspecify.annotations.Nullable;

public interface MultiDimServer {
    void multidim$addWorld(ServerLevel world);
    boolean multidim$hasWorld(ResourceKey<Level> key);
    @Nullable ServerLevel multidim$removeWorld(ResourceKey<Level> key);

    LevelStorageSource.LevelStorageAccess multidim$getSession();
}

