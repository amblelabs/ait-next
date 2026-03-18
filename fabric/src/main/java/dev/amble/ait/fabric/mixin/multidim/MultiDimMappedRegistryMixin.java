package dev.amble.ait.fabric.mixin.multidim;

import com.mojang.serialization.Lifecycle;
import dev.amble.lib.multidim.api.MutableRegistry;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Optional;

@Mixin(MappedRegistry.class)
public abstract class MultiDimMappedRegistryMixin<T> implements MutableRegistry<T> {

    @Shadow @Final private Map<ResourceLocation, Holder.Reference<T>> byLocation;
    @Shadow @Final private Map<T, Holder.Reference<T>> byValue;
    @Shadow @Final private Reference2IntMap<T> toId;
    @Shadow @Final private ObjectList<Holder.Reference<T>> byId;
    @Shadow @Final private Map<ResourceKey<T>, Holder.Reference<T>> byKey;
    @Shadow @Final private Map<T, RegistrationInfo> registrationInfos;
    @Shadow private boolean frozen;

    @Shadow public abstract Holder.Reference<T> register(ResourceKey<T> key, T entry, RegistrationInfo registrationInfo);
    @Shadow public abstract boolean containsKey(ResourceKey<T> key);

    @Override
    public boolean multidim$remove(T entry) {
        Holder.Reference<T> holder = this.byValue.get(entry);
        int rawId = this.toId.removeInt(entry);
        if (holder == null || rawId == -1) {
            return false;
        }

        this.byLocation.remove(holder.key().location());
        this.byKey.remove(holder.key());
        this.registrationInfos.remove(entry);
        this.byValue.remove(entry);

        if (rawId < this.byId.size()) {
            this.byId.set(rawId, null);
        }

        return true;
    }

    @Override
    public boolean multidim$remove(ResourceLocation key) {
        Holder.Reference<T> holder = this.byLocation.get(key);
        return holder != null && this.multidim$remove(holder.value());
    }

    @Override
    public void multidim$freeze() {
        this.frozen = true;
    }

    @Override
    public void multidim$unfreeze() {
        this.frozen = false;
    }

    @Override
    public boolean multidim$isFrozen() {
        return this.frozen;
    }

    @Override
    public boolean multidim$contains(ResourceKey<T> key) {
        return this.containsKey(key);
    }

    @Override
    public Holder.Reference<T> multidim$add(ResourceKey<T> key, T entry, Lifecycle lifecycle) {
        return this.register(key, entry, new RegistrationInfo(Optional.empty(), lifecycle));
    }
}


