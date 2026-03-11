package dev.amble.ait.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static dev.amble.ait.api.AitAPI.modLoc;

// https://github.com/VazkiiMods/Botania/blob/1.19.x/Xplat/src/main/java/vazkii/botania/client/model/BotaniaModelLayers.java
public class AitModelLayers {

    private static ModelLayerLocation make(String name) {
        return make(name, "main");
    }

    private static ModelLayerLocation make(String name, String layer) {
        // Don't add to vanilla's ModelLayers. It seems to only be used for error checking
        // And would be annoying to do under Forge's parallel mod loading
        return new ModelLayerLocation(modLoc(name), layer);
    }

    // moving this stuff into the same file:
    // https://github.com/VazkiiMods/Botania/blob/1.19.x/Xplat/src/main/java/vazkii/botania/client/model/BotaniaLayerDefinitions.java
    public static void init(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {

    }
}