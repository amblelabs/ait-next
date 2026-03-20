package dev.amble.ait.client.model;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.blocks.DoorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DoorGeoModel extends GeoModel<DoorBlockEntity> {

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getModelResource(DoorBlockEntity entity) {
        return AitAPI.modLoc("geo/blockentities/" + entity.getModelName() + "_door.geo.json");
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getTextureResource(DoorBlockEntity entity) {
        return AitAPI.modLoc("textures/blockentities/exteriors/" + entity.getTextureName() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(DoorBlockEntity entity) {
        return AitAPI.modLoc("animations/blockentities/" + entity.getModelName() + "_door.animation.json");
    }
}

