package dev.amble.lib.multidim.api;

import com.mojang.serialization.Lifecycle;
import dev.amble.lib.multidim.impl.AbstractWorldGenListener;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Executor;

public class WorldBlueprint {

    private final ResourceLocation id;

    private long seed;
    private boolean tickTime = true;

    private @Nullable ResourceLocation typeId;
    private @Nullable DimensionType type;

    private WorldCreator creator = MultiDimServerLevel::new;
    private @Nullable ChunkGenerator generator;

    private boolean autoLoad = true;
    private boolean persistent = true;
    private @Nullable LevelStem options;

    public WorldBlueprint(ResourceLocation id) {
        this.id = id;
    }

    public WorldBlueprint withSeed(long seed) {
        this.seed = BiomeManager.obfuscateSeed(seed);
        return this;
    }

    public long seed() {
        return this.seed;
    }

    public WorldBlueprint withCreator(WorldCreator creator) {
        this.creator = creator;
        return this;
    }

    public WorldBlueprint withType(ResourceLocation id) {
        this.typeId = id;
        return this;
    }

    public WorldBlueprint withType(DimensionType type) {
        return this.withType(null, type);
    }

    public WorldBlueprint withType(@Nullable ResourceLocation id, DimensionType type) {
        this.typeId = id;
        this.type = type;
        return this;
    }

    public WorldBlueprint withGenerator(ChunkGenerator generator) {
        this.generator = generator;
        return this;
    }

    public WorldBlueprint shouldTickTime(boolean tickTime) {
        this.tickTime = tickTime;
        return this;
    }

    public boolean shouldTickTime() {
        return this.tickTime;
    }

    public ResourceLocation id() {
        return this.id;
    }

    public WorldBlueprint setPersistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    public boolean persistent() {
        return this.persistent;
    }

    public WorldBlueprint setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
        return this;
    }

    public boolean autoLoad() {
        return this.autoLoad;
    }

    public MultiDimServerLevel createWorld(MinecraftServer server, ResourceKey<Level> key, LevelStem levelStem, boolean created) {
        WorldData worldData = server.getWorldData();
        ServerLevelData levelData = new DerivedLevelData(worldData, worldData.overworldData());

        return this.creator.create(
                this,
                server,
                Util.backgroundExecutor(),
                ((MultiDimServer) server).multidim$getSession(),
                levelData,
                key,
                levelStem,
                new AbstractWorldGenListener(),
                List.of(),
                this.shouldTickTime(),
                null,
                created
        );
    }

    private Holder<DimensionType> resolveType(MinecraftServer server) {
        Registry<DimensionType> typeRegistry = server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);

        if (this.typeId == null) {
            this.typeId = this.id;
        }

        ResourceKey<DimensionType> typeKey = ResourceKey.create(Registries.DIMENSION_TYPE, this.typeId);

        if (this.type == null) {
            return typeRegistry.getHolder(typeKey).orElse(null);
        }

        if (!typeRegistry.containsKey(typeKey)) {
            if (typeRegistry instanceof MutableRegistry<?> mutableRegistry) {
                @SuppressWarnings("unchecked")
                MutableRegistry<DimensionType> mutable = (MutableRegistry<DimensionType>) mutableRegistry;
                return mutable.multidim$add(typeKey, this.type, Lifecycle.stable());
            }

            throw new IllegalStateException("Dimension type registry is not mutable for custom type " + typeKey.location());
        }

        return typeRegistry.getHolder(typeKey).orElse(null);
    }

    public LevelStem createOptions(MinecraftServer server) {
        if (this.options != null) {
            return this.options;
        }

        if (this.generator == null) {
            throw new IllegalArgumentException("Chunk generator is required to create a level stem");
        }

        Holder<DimensionType> typeEntry = this.resolveType(server);

        if (typeEntry == null) {
            throw new IllegalArgumentException("Dimension type is required to create level stem options");
        }

        this.options = new LevelStem(typeEntry, this.generator);
        return this.options;
    }

    public interface WorldCreator {
        MultiDimServerLevel create(WorldBlueprint blueprint, MinecraftServer server, Executor workerExecutor,
                                   LevelStorageSource.LevelStorageAccess session, ServerLevelData properties,
                                   ResourceKey<Level> worldKey, LevelStem levelStem,
                                   ChunkProgressListener worldGenerationProgressListener,
                                   List<CustomSpawner> spawners, boolean shouldTickTime,
                                   @Nullable RandomSequences randomSequences, boolean created);
    }
}

