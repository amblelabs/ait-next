package dev.amble.ait.client.impl.tardis;

import dev.amble.ait.api.tardis.Tardis;
import dev.amble.ait.api.tardis.TardisManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class ClientTardisManager implements TardisManager<Tardis> {

    private static @Nullable ClientTardisManager INSTANCE;

    private final Object2ObjectMap<UUID, Tardis> lookup = new Object2ObjectOpenHashMap<>();

    public ClientTardisManager() {
        if (INSTANCE != null)
            INSTANCE.clear();

        INSTANCE = this;
    }

    public void tick() {
        for (Tardis stargate : this.lookup.values()) {
            stargate.tick();
        }
    }

    @Override
    public void add(Tardis tardis) {
        this.lookup.put(tardis.id(), tardis);
    }

    public void upsert(CompoundTag tag) {
        UUID id = tag.getUUID(Tardis.ID_TAG);
        Tardis tardis = this.get(id);

        if (tardis == null) {
            tardis = Tardis.fromNbt(tag, true);
            this.add(tardis);
        } else {
            tardis.updateStates(tag, true, false);
        }
    }

    @Override
    public void remove(UUID id) {
        this.lookup.remove(id);
    }

    @Override
    public boolean contains(UUID id) {
        return lookup.containsKey(id);
    }

    @Override
    public @Nullable Tardis get(UUID id) {
        return lookup.get(id);
    }

    private void clear() {
        this.lookup.clear();
    }

    public static ClientTardisManager get() {
        return INSTANCE;
    }
}