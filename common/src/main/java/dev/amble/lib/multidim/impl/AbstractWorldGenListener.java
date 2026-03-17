package dev.amble.lib.multidim.impl;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class AbstractWorldGenListener implements ChunkProgressListener {

    @Override
    public void updateSpawnPos(ChunkPos spawnPos) { }

    @Override
    public void onStatusChange(ChunkPos pos, @Nullable ChunkStatus status) { }

    @Override
    public void start() { }

    @Override
    public void stop() { }
}


