package dev.amble.ait.fabric.datagen.lang;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.api.mod.AitTags;
import dev.amble.ait.common.lib.AitItems;
import dev.amble.lib.fabric.datagen.FabricAmbleLangProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class FabricAitLangProvider {

    public static class EnUs extends FabricAmbleLangProvider {

        public EnUs(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, "en_us", registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder builder) {
            builder.add(AitItems.SCREWDRIVER, "Screwdriver");
            builder.add(AitItems.SCREWDRIVER.getDescriptionId() + ".desc", "Just a normal screwdriver.");

            builder.add(AitItems.SONIC_SCREWDRIVER, "Sonic Screwdriver");
            builder.add(AitItems.SONIC_SCREWDRIVER.getDescriptionId() + ".desc", "Just a normal screwdriver- oop, nope, nevermind.");

            // Zeiton Shards
            builder.add(AitTags.Items.ZEITON_SHARDS, "Zeiton Shards");

            builder.add(AitItems.SHARD_AMETHYST, "Amethyst Zeiton Shard");
            builder.add(AitItems.SHARD_BASIC, "Basic Zeiton Shard");
            builder.add(AitItems.SHARD_GRAVITY, "Gravitational Zeiton Shard");
            builder.add(AitItems.SHARD_OVERCHARGED, "Overcharged Zeiton Shard");
            builder.add(AitItems.SHARD_QUARTZ, "Quartz Zeiton Shard");
            builder.add(AitItems.SHARD_REFRACTION, "Refractional Zeiton Shard");
            builder.add(AitItems.SHARD_RESONATING, "Resonating Zeiton Shard");
            builder.add(AitItems.SHARD_SCULK, "Sculk Zeiton Shard");

            // Widgets
            builder.add("widget." + AitAPI.MOD_ID + ".empty", "Empty");
            builder.add("widget." + AitAPI.MOD_ID + ".empty.desc", "...");
        }
    }
}
