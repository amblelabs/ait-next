package dev.amble.ait.common.impl.tardis.behavior;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.api.tardis.ServerTardis;
import dev.amble.ait.api.tardis.Tardis;
import dev.amble.ait.api.tardis.TardisManager;
import dev.amble.ait.api.tardis.event.ServerLifecycleEvents;
import dev.amble.ait.api.tardis.event.desktop.DesktopPlacementEvents;
import dev.amble.ait.api.tardis.event.init.TardisLifecycleEvents;
import dev.amble.ait.common.impl.tardis.ServerTardisManager;
import dev.amble.ait.common.impl.tardis.TardisServerWorld;
import dev.amble.ait.common.impl.tardis.state.DesktopState;
import dev.amble.ait.common.impl.tardis.state.DimensionState;
import dev.drtheo.ecs.behavior.TBehavior;
import dev.drtheo.multidim.MultiDim;
import dev.drtheo.multidim.api.VoidChunkGenerator;
import dev.drtheo.multidim.api.WorldBlueprint;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class DimensionBehavior implements TBehavior, TardisLifecycleEvents, ServerLifecycleEvents {

    public @Nullable WorldBlueprint blueprint;

    @Override
    public void onPostCreated(ServerTardis tardis, MinecraftServer server) {
        DimensionState state = tardis.state(DimensionState.state);
        ServerLevel level = TardisServerWorld.create(server, Objects.requireNonNull(this.blueprint), tardis);

        state.level = new WeakReference<>(level);

        // Trigger desktop placement, currently a hollow shell.
        DesktopPlacementEvents.handleBeforePlaced(tardis, server);
    }

    @Override
    public void onLoaded(TardisManager<?> manager, Tardis tardis) {
        if (!(manager instanceof ServerTardisManager stm) || !(tardis instanceof ServerTardis serverTardis)) return;

        DimensionState state = tardis.state(DimensionState.state);
        ServerLevel level = TardisServerWorld.getOrLoad(stm.server(), Objects.requireNonNull(this.blueprint), serverTardis);

        state.level = new WeakReference<>(level);
    }

    @Override
    public void onServerStarted(MinecraftServer server) {
        blueprint = new WorldBlueprint(AitAPI.modLoc("tardis"))
                .setPersistent(false).shouldTickTime(false)
                .setAutoLoad(false).withCreator(TardisServerWorld::new)
                .withType(AitAPI.modLoc("tardis"))
                .withGenerator(new VoidChunkGenerator(
                        server.registryAccess().registry(Registries.BIOME).orElseThrow(),
                        ResourceKey.create(Registries.BIOME, AitAPI.modLoc("tardis"))
                ));

        MultiDim.get(server).register(blueprint);
    }

    @Override
    public void onServerStopping(MinecraftServer server) {
        blueprint = null;
    }
}
