package dev.amble.ait.common.impl.tardis;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.api.tardis.ServerTardis;
import dev.drtheo.multidim.MultiDim;
import dev.drtheo.multidim.api.MultiDimServerLevel;
import dev.drtheo.multidim.api.WorldBlueprint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

public class TardisServerWorld extends MultiDimServerLevel {

    public static final String NAMESPACE = AitAPI.MOD_ID + "-tardis";

    private @Nullable ServerTardis tardis;
    private @Nullable Holder<Biome> cachedBiome;

    public TardisServerWorld(WorldBlueprint blueprint, MinecraftServer server, Executor workerExecutor, LevelStorageSource.LevelStorageAccess session, ServerLevelData properties, ResourceKey<Level> worldKey, LevelStem levelStem, ChunkProgressListener worldGenerationProgressListener, List<CustomSpawner> spawners, boolean shouldTickTime, @Nullable RandomSequences randomSequences, boolean created) {
        super(blueprint, server, workerExecutor, session, properties, worldKey, levelStem, worldGenerationProgressListener, spawners, shouldTickTime, randomSequences, created);
        super.setSpawnSettings(false, false);
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking) {
        if (this.shouldTick()) {
            super.tick(shouldKeepTicking);
        }
    }

    public boolean shouldTick() {
        return this.tardis != null && (
                !MultiDim.get(this.getServer()).isWorldUnloaded(this)
        );
    }

    @Override
    public String toString() {
        return "Tardis" + super.toString();
    }

    @Override
    public Holder<Biome> getBiome(BlockPos pos) {
        if (this.cachedBiome != null)
            return cachedBiome;

        return this.cachedBiome = super.getBiome(pos);
    }

    public @Nullable ServerTardis tardis() {
        return tardis;
    }

    public static TardisServerWorld create(MinecraftServer server, WorldBlueprint blueprint, ServerTardis tardis) {
        AitAPI.LOGGER.info("Creating a dimension for TARDIS {}", tardis.id());

        TardisServerWorld created = (TardisServerWorld) MultiDim.get(server)
                .add(blueprint, idForTardis(tardis));

        created.tardis = tardis;
        return created;
    }

    public static TardisServerWorld getOrLoad(MinecraftServer server, WorldBlueprint blueprint, ServerTardis tardis) {
        ResourceKey<Level> key = keyForTardis(tardis);
        TardisServerWorld result = (TardisServerWorld) server.getLevel(key);

        if (result != null) {
            result.tardis = tardis;
            return result;
        }

        return load(server, blueprint, tardis);
    }

    public static TardisServerWorld load(MinecraftServer server, WorldBlueprint blueprint, ServerTardis tardis) {
        MultiDim multidim = MultiDim.get(server);

        ResourceKey<Level> key = keyForTardis(tardis);
        TardisServerWorld result = (TardisServerWorld) multidim.load(blueprint, key);

        result.tardis = tardis;
        return result;
    }

    public static ResourceKey<Level> keyForTardis(ServerTardis tardis) {
        return ResourceKey.create(Registries.DIMENSION, idForTardis(tardis));
    }

    private static ResourceLocation idForTardis(ServerTardis tardis) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, tardis.id().toString());
    }

    public static boolean isTardisDimension(ResourceKey<Level> key) {
        return NAMESPACE.equals(key.location().getNamespace());
    }

    public static boolean isTardisDimension(Level world) {
        return world.isClientSide() ? isTardisDimension((ClientLevel) world) : isTardisDimension((ServerLevel) world);
    }

    public static boolean isTardisDimension(ServerLevel world) {
        return world instanceof TardisServerWorld;
    }

    public static @Nullable UUID getTardisId(@Nullable Level world) {
        if (world == null || !isTardisDimension(world))
            return null;

        return getTardisId(world.dimension());
    }

    public static UUID getTardisId(ResourceKey<Level> key) {
        return UUID.fromString(key.location().getPath());
    }

    @Environment(EnvType.CLIENT)
    public static boolean isTardisDimension(ClientLevel world) {
        return isTardisDimension(world.dimension());
    }
}