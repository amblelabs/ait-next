package dev.amble.ait.client.model;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.blocks.FallingTardisBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FallingTardisGeoModel extends GeoModel<FallingTardisBlockEntity> {

    private static final ResourceLocation MODEL = AitAPI.modLoc("geo/blockentities/police_box.geo.json");
    private static final ResourceLocation ANIMATION = AitAPI.modLoc("animations/blockentities/police_box.animation.json");

    private static final ResourceLocation[] TEXTURES = {
            AitAPI.modLoc("textures/blockentities/exteriors/police_box_default.png"),
            AitAPI.modLoc("textures/blockentities/exteriors/police_box_coral.png"),
            AitAPI.modLoc("textures/blockentities/exteriors/police_box_renaissance.png"),
            AitAPI.modLoc("textures/blockentities/exteriors/police_box_crystalline.png"),
            AitAPI.modLoc("textures/blockentities/exteriors/police_box_future.png"),
    };

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getModelResource(FallingTardisBlockEntity entity) {
        return MODEL;
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getTextureResource(FallingTardisBlockEntity entity) {
        return TEXTURES[entity.getTextureIndex() % TEXTURES.length];
    }

    @Override
    public ResourceLocation getAnimationResource(FallingTardisBlockEntity entity) {
        return ANIMATION;
    }
}

