package dev.amble.ait.client.model;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.blocks.ConsoleBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ConsoleGeoModel extends GeoModel<ConsoleBlockEntity> {

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getModelResource(ConsoleBlockEntity entity) {
        return AitAPI.modLoc("geo/blockentities/" + entity.getModelName() + ".geo.json");
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getTextureResource(ConsoleBlockEntity entity) {
        return AitAPI.modLoc("textures/blockentities/consoles/" + entity.getTextureName() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(ConsoleBlockEntity entity) {
        return AitAPI.modLoc("animations/blockentities/" + entity.getAnimationName() + ".animation.json");
    }
}

