package dev.amble.ait.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static dev.amble.ait.api.AitAPI.modLoc;

public class AitLootFunctions {
    public static void registerSerializers(BiConsumer<LootItemFunctionType, ResourceLocation> r) {
        for (var e : LOOT_FUNCS.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    private static final Map<ResourceLocation, LootItemFunctionType> LOOT_FUNCS = new LinkedHashMap<>();

    //

    private static LootItemFunctionType register(String id, LootItemFunctionType lift) {
        var old = LOOT_FUNCS.put(modLoc(id), lift);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        return lift;
    }
}