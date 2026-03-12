package dev.amble.ait.common.lib;

import dev.amble.ait.common.blocks.FallingTardisBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

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

    public static final EntityType<FallingTardisBlockEntity> FALLING_TARDIS_BLOCK =
            register("falling_tardis_block", EntityType.Builder
                    .<FallingTardisBlockEntity>of(FallingTardisBlockEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .build("falling_tardis_block"));

    @SuppressWarnings("SameParameterValue")
    private static <T extends Entity> EntityType<T> register(String id, EntityType<T> type) {
        var old = ENTITIES.put(modLoc(id), type);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        return type;
    }
}