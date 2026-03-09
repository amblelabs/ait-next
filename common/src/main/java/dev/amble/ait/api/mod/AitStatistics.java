package dev.amble.ait.api.mod;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

import static dev.amble.ait.api.AitAPI.modLoc;

public class AitStatistics {
    //

    public static void register() {
        // wake up java
    }

    private static ResourceLocation makeCustomStat(String key, StatFormatter formatter) {
        ResourceLocation resourcelocation = modLoc(key);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, key, resourcelocation);
        Stats.CUSTOM.get(resourcelocation, formatter);
        return resourcelocation;
    }
}