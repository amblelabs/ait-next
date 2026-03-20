package dev.amble.ait.client.model;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.blocks.ExteriorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;

public class PoliceBoxGeoModel extends GeoModel<ExteriorBlockEntity> {

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getModelResource(ExteriorBlockEntity entity) {
        return AitAPI.modLoc("geo/blockentities/" + entity.getModelName() + ".geo.json");
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getTextureResource(@Nullable ExteriorBlockEntity entity) {
        if (entity == null) return AitAPI.modLoc("textures/blockentities/exteriors/police_box_default.png");
        return AitAPI.modLoc("textures/blockentities/exteriors/" + entity.getTextureName() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(ExteriorBlockEntity entity) {
        return AitAPI.modLoc("animations/blockentities/" + entity.getModelName() + ".animation.json");
    }
}
