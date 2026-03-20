package dev.amble.ait.mixin.tardis.manager;

import dev.amble.ait.api.tardis.ServerTardis;
import dev.amble.ait.api.tardis.TardisManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements TardisManager.ManagerLevel<ServerTardis>, TardisManager.ChunkTracker {

    @Shadow public abstract MinecraftServer getServer();

    @Unique
    private final Long2ObjectMap<@Nullable Set<ServerTardis>> ait$marks = new Long2ObjectOpenHashMap<>();

    @Override
    public TardisManager<ServerTardis> ait$initTardisManager() {
        return TardisManager.<ServerTardis>asManagerLevel(this.getServer()).ait$initTardisManager();
    }

    @Override
    public @Nullable TardisManager<ServerTardis> ait$getTardisManager() {
        return TardisManager.<ServerTardis>asManagerLevel(this.getServer()).ait$getTardisManager();
    }

    @Override
    public void ait$mark(long pos, ServerTardis tardis) {
        ait$marks.computeIfAbsent(pos, k -> new HashSet<>())
                .add(tardis);
    }

    @Override
    public void ait$unmark(long pos, ServerTardis tardis) {
        Set<ServerTardis> set = ait$marks.get(pos);
        if (set == null) return;

        set.remove(tardis);

        if (set.isEmpty())
            ait$marks.remove(pos);
    }

    @Override
    public @Nullable Collection<ServerTardis> ait$getMarked(long pos) {
        return ait$marks.get(pos);
    }
}
