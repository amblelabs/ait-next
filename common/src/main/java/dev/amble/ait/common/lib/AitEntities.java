package dev.amble.ait.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static dev.amble.ait.api.AitAPI.modLoc;

public class AitEntities {
    public static void registerEntities(BiConsumer<EntityType<?>, ResourceLocation> r) {
        for (var e : ENTITIES.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    private static final Map<ResourceLocation, EntityType<?>> ENTITIES = new LinkedHashMap<>();

    //

    private static <T extends Entity> EntityType<T> register(String id, EntityType<T> type) {
        var old = ENTITIES.put(modLoc(id), type);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        return type;
    }
}