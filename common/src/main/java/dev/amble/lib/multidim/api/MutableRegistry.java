package dev.amble.lib.multidim.api;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.RegistrationInfo;

public interface MutableRegistry<T> {
    boolean multidim$remove(T entry);
    boolean multidim$remove(ResourceLocation key);

    void multidim$freeze();
    void multidim$unfreeze();
    boolean multidim$isFrozen();

    boolean multidim$contains(ResourceKey<T> key);
    Holder.Reference<T> multidim$add(ResourceKey<T> key, T entry, Lifecycle lifecycle);
}

