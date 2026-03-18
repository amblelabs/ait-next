package dev.amble.ait.common.impl.tardis;

import dev.amble.ait.api.mod.storage.PlainLazyDirectoryDimensionDataStorage;
import dev.amble.ait.api.mod.tardis.ServerTardis;
import dev.amble.ait.api.mod.tardis.TardisManager;
import dev.amble.ait.common.network.tardis.manager.TardisSyncPayload;
import dev.amble.ait.xplat.IXplatAbstractions;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class ServerTardisManager implements TardisManager<ServerTardis> {

	private final Object2ObjectMap<UUID, @Nullable ServerTardis> lookup = new Object2ObjectOpenHashMap<>();
	private final ServerLevel level; // lives just as long as the world, shouldn't explode

	public ServerTardisManager(ServerLevel level) {
		this.level = level;
	}

	public void tick() {
		for (ServerTardis tardis : this.lookup.values()) {
			if (tardis == null) continue;

			tardis.tick();

			if (tardis.dirty()) {
				this.syncPartial(tardis, this.level.players().stream());
				tardis.unmarkDirty();
			}
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
		CompoundTag data = PlainLazyDirectoryDimensionDataStorage.get(level).readSavedData(id.toString(), 0);
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
		PlainLazyDirectoryDimensionDataStorage storage = PlainLazyDirectoryDimensionDataStorage.get(level);

		for (ServerTardis tardis : this.lookup.values()) {
			if (tardis == null) continue;

			CompoundTag tag = new CompoundTag();
			tardis.toNbt(tag, false);

			storage.save(tardis.id().toString(), tag);
		}
	}
}