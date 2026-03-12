package dev.amble.ait.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static dev.amble.ait.api.AitAPI.modLoc;

public class AitAttributes {
    public static void register(BiConsumer<Attribute, ResourceLocation> r) {
        for (var e : ATTRIBUTES.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    private static final Map<ResourceLocation, Attribute> ATTRIBUTES = new LinkedHashMap<>();

    //

    @SuppressWarnings("unused")
    private static <T extends Attribute> T make(String id, T attr) {
        var old = ATTRIBUTES.put(modLoc(id), attr);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        return attr;
    }
}