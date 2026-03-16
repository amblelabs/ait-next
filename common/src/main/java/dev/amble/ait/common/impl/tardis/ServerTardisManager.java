package dev.amble.ait.common.impl.tardis;

import dev.amble.ait.api.mod.storage.PlainLazyDirectoryDimensionDataStorage;
import dev.amble.ait.api.mod.tardis.ServerTardis;
import dev.amble.ait.api.mod.tardis.TardisManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.*;

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

//			if (tardis.dirty()) {
//				this.syncPartial(tardis.id(), tardis, tracking(tardis));
//				tardis.unmarkDirty();
//			}
		}
	}

//	public void syncAll(Collection<ServerPlayer> targets) {
//		PacketByteBuf buf = PacketByteBufs.create();
//		buf.writeNbt(this.toNbt(true));
//
//		targets.forEach(player ->
//				ServerPlayNetworking.send(player, SYNC_ALL, buf));
//	}
//
//	public void syncPartial(long id, ServerTardis gate, Stream<ServerPlayer> targets) {
//		NbtCompound nbt = new NbtCompound();
//		gate.toNbt(nbt, true);
//
//		PacketByteBuf buf = PacketByteBufs.create();
//		buf.writeVarLong(id);
//		buf.writeNbt(nbt);
//
//		targets.forEach(player ->
//				ServerPlayNetworking.send(player, SYNC, buf));
//	}

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