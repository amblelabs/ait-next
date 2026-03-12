package dev.amble.ait.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static dev.amble.ait.api.AitAPI.modLoc;

@SuppressWarnings("unused")
public class AitMobEffects {
    public static void register(BiConsumer<MobEffect, ResourceLocation> r) {
        for (var e : EFFECTS.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    private static final Map<ResourceLocation, MobEffect> EFFECTS = new LinkedHashMap<>();

    //

    private static <T extends MobEffect> T make(String id, T effect) {
        var old = EFFECTS.put(modLoc(id), effect);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        return effect;
    }
}