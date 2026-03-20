package dev.drtheo.multidim.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.server.level.WorldGenRegion;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractChunkGenerator extends ChunkGenerator {

    public AbstractChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState,
                              BiomeManager biomeManager, StructureManager structureManager,
                              ChunkAccess chunk, GenerationStep.Carving carverStep) { }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager,
                              RandomState randomState, ChunkAccess chunk) { }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) { }

    @Override
    public int getGenDepth() {
        return 0;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world,
                              RandomState randomState) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState randomState) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) { }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState,
                                                         StructureManager structureManager,
                                                         ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk,
                                      StructureManager structureManager) { }

    @Override
    public void createReferences(WorldGenLevel world, StructureManager structureManager,
                                  ChunkAccess chunk) { }
}


