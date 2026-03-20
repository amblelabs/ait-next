package dev.amble.ait.common.impl.tardis;

import dev.amble.ait.api.mod.storage.PlainLazyDirectoryDimensionDataStorage;
import dev.amble.ait.api.tardis.ServerTardis;
import dev.amble.ait.api.tardis.TardisManager;
import dev.amble.ait.common.network.tardis.manager.TardisSyncPayload;
import dev.amble.ait.xplat.IXplatAbstractions;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class ServerTardisManager implements TardisManager<ServerTardis> {

	private final Object2ObjectMap<UUID, @Nullable ServerTardis> lookup = new Object2ObjectOpenHashMap<>();
	private final MinecraftServer server; // lives just as long as the world, shouldn't explode

	public ServerTardisManager(MinecraftServer server) {
		this.server = server;
	}

	public void tick() {
		for (ServerTardis tardis : this.lookup.values()) {
			if (tardis == null) continue;

			tardis.tick();

			if (tardis.dirty()) {
				this.syncPartial(tardis, server.getPlayerList().getPlayers().stream());
				tardis.unmarkDirty();
			}
		}
	}

	public void sendInChunk(ServerPlayer player, ServerLevel level, LevelChunk chunk) {
		Collection<ServerTardis> marked = TardisManager.asChunkTracker(level).ait$getMarked(chunk.getPos());
		if (marked == null) return;

		for (ServerTardis tardis : marked) {
			this.syncPartial(tardis, Stream.of(player));
		}
	}

	public void syncPartial(ServerTardis tardis, Stream<ServerPlayer> targets) {
		CompoundTag nbt = new CompoundTag();
		tardis.toNbt(nbt, true);

		TardisSyncPayload payload = new TardisSyncPayload(nbt);
		IXplatAbstractions.INSTANCE.sendPacketToAll(targets, payload);
	}

	@Override
	public boolean contains(UUID id) {
		return lookup.containsKey(id);
	}

	private @Nullable ServerTardis load(UUID id) {
		CompoundTag data = PlainLazyDirectoryDimensionDataStorage.get(server).readSavedData(id.toString(), 0);
		if (data == null) return null;

		return ServerTardis.fromNbt(data);
	}

	@Override
	public @Nullable ServerTardis get(UUID id) {
		return lookup.computeIfAbsent(id, this::load);
	}

	@Override
	public void remove(UUID id) {
		lookup.remove(id);
	}

	@Override
	public void add(ServerTardis tardis) {
		lookup.put(tardis.id(), tardis);
	}

	public void save() {
		PlainLazyDirectoryDimensionDataStorage storage = PlainLazyDirectoryDimensionDataStorage.get(server);

		for (ServerTardis tardis : this.lookup.values()) {
			if (tardis == null) continue;

			CompoundTag tag = new CompoundTag();
			tardis.toNbt(tag, false);

			storage.save(tardis.id().toString(), tag);
		}
	}
}