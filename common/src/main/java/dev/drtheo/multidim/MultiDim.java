package dev.drtheo.multidim;

import com.mojang.serialization.Lifecycle;
import dev.drtheo.multidim.api.MultiDimServer;
import dev.drtheo.multidim.api.MultiDimServerLevel;
import dev.drtheo.multidim.api.MutableRegistry;
import dev.drtheo.multidim.api.WorldBlueprint;
import dev.drtheo.multidim.impl.SimpleWorldProgressListener;
import dev.drtheo.multidim.util.MultiDimUtil;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiDim {

    private static @Nullable MultiDim instance;

    private final Map<ResourceLocation, WorldBlueprint> blueprints = new HashMap<>();
    private final MinecraftServer server;

    private final Set<ServerLevel> toDelete = new ReferenceOpenHashSet<>();
    private final Set<ServerLevel> toUnload = new ReferenceOpenHashSet<>();

    private MultiDim(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer server() {
        return this.server;
    }

    public void tick() {
        if (!this.toDelete.isEmpty()) {
            this.toDelete.removeIf(this::tickDeleteWorld);
        }
        if (!this.toUnload.isEmpty()) {
            this.toUnload.removeIf(this::tickUnloadWorld);
        }
    }

    public boolean isWorldUnloaded(ServerLevel world) {
        return world.players().isEmpty() && world.getChunkSource().getPendingTasksCount() <= 0;
    }

    private boolean prepareForUnload(ServerLevel world) {
        if (this.isWorldUnloaded(world)) {
            return true;
        }

        this.kickPlayers(world);
        return false;
    }

    public void kickPlayers(ServerLevel world) {
        if (world.players().isEmpty()) {
            return;
        }

        ServerLevel overworld = this.server.overworld();
        BlockPos spawnPos = overworld.getSharedSpawnPos();

        for (ServerPlayer player : world.players()) {
            player.teleportTo(overworld, spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, player.getYRot(), player.getXRot());
        }
    }

    private boolean tickDeleteWorld(ServerLevel world) {
        if (!this.prepareForUnload(world)) {
            return false;
        }

        this.remove(world.dimension());
        return true;
    }

    private boolean tickUnloadWorld(ServerLevel world) {
        if (!this.prepareForUnload(world)) {
            return false;
        }

        this.unload(world.dimension());
        return true;
    }

    public void register(WorldBlueprint blueprint) {
        this.blueprints.put(blueprint.id(), blueprint);
    }

    public static MultiDim get(MinecraftServer server) {
        if (instance == null || instance.server != server) {
            instance = new MultiDim(server);
        }

        return instance;
    }

    public MultiDimServerLevel add(WorldBlueprint blueprint, ResourceLocation id) {
        return this.addOrLoad(blueprint, id, true);
    }

    public MultiDimServerLevel load(WorldBlueprint blueprint, ResourceLocation id) {
        return this.addOrLoad(blueprint, id, false);
    }

    public MultiDimServerLevel addOrLoad(WorldBlueprint blueprint, ResourceLocation id, boolean created) {
        return this.addOrLoad(blueprint, ResourceKey.create(Registries.DIMENSION, id), created);
    }

    public MultiDimServerLevel add(WorldBlueprint blueprint, ResourceKey<Level> id) {
        return this.addOrLoad(blueprint, id, true);
    }

    public MultiDimServerLevel load(WorldBlueprint blueprint, ResourceKey<Level> id) {
        return this.addOrLoad(blueprint, id, false);
    }

    public MultiDimServerLevel addOrLoad(WorldBlueprint blueprint, ResourceKey<Level> id, boolean created) {
        ServerLevel existing = this.server.getLevel(id);
        if (existing != null) {
            if (existing instanceof MultiDimServerLevel multiDimServerLevel) {
                return multiDimServerLevel;
            }

            throw new IllegalStateException("World " + id.location() + " is already loaded as " + existing.getClass().getName());
        }

        MutableRegistry<LevelStem> dimensionsRegistry = MultiDimUtil.getMutableDimensionsRegistry(this.server);
        boolean wasFrozen = dimensionsRegistry.multidim$isFrozen();
        if (wasFrozen) {
            dimensionsRegistry.multidim$unfreeze();
        }

        LevelStem options = blueprint.createOptions(this.server);
        ResourceKey<LevelStem> key = ResourceKey.create(Registries.LEVEL_STEM, id.location());
        if (!dimensionsRegistry.multidim$contains(key)) {
            dimensionsRegistry.multidim$add(key, options, Lifecycle.stable());
        }

        if (wasFrozen) {
            dimensionsRegistry.multidim$freeze();
        }

        MultiDimServerLevel world = blueprint.createWorld(this.server, id, options, created);
        this.load(world);
        return world;
    }

    public void queueUnload(MultiDimServerLevel world) {
        this.toUnload.add(world);
    }

    public void queueUnload(ResourceKey<Level> key) {
        ServerLevel world = this.server.getLevel(key);
        if (world != null) {
            this.toUnload.add(world);
        }
    }

    private void unload(ResourceKey<Level> key) {
        ServerLevel world = ((MultiDimServer) this.server).multidim$removeWorld(key);
        if (world == null) return;

        world.save(new SimpleWorldProgressListener(() -> {
            MultiDimUtil.getMutableDimensionsRegistry(this.server).multidim$remove(key.location());
            this.refreshCommandTrees();
        }), true, false);
    }

    public void queueRemove(MultiDimServerLevel world) {
        this.toDelete.add(world);
    }

    public void queueRemove(ResourceKey<Level> key) {
        ServerLevel world = this.server.getLevel(key);
        if (world != null) {
            this.toDelete.add(world);
        }
    }

    private void remove(ResourceKey<Level> key) {
        ServerLevel world = ((MultiDimServer) this.server).multidim$removeWorld(key);

        if (world == null) return;

        MultiDimUtil.getMutableDimensionsRegistry(this.server).multidim$remove(key.location());
        this.refreshCommandTrees();

        Path worldDirectory = ((MultiDimServer) this.server).multidim$getSession().getDimensionPath(key);

        if (!Files.exists(worldDirectory)) {
            return;
        }

        try (var walk = Files.walk(worldDirectory)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Throwable e) {
            MultiDimMod.LOGGER.warn("Failed to delete world directory {}", worldDirectory, e);
        }
    }

    private void load(MultiDimServerLevel world) {
        MultiDimMod.LOGGER.info("Loading world {}", world.dimension().location());

        if (((MultiDimServer) this.server).multidim$hasWorld(world.dimension())) {
            MultiDimMod.LOGGER.warn("World {} is already loaded", world.dimension().location());
            return;
        }

        ((MultiDimServer) this.server).multidim$addWorld(world);
        world.tick(() -> true);
        this.refreshCommandTrees();
    }

    private void refreshCommandTrees() {
        this.server.getPlayerList().getPlayers().forEach(player -> this.server.getCommands().sendCommands(player));
    }

    public @Nullable WorldBlueprint getBlueprint(ResourceLocation id) {
        return this.blueprints.get(id);
    }
}


