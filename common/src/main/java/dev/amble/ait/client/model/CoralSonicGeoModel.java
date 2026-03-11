package dev.amble.ait.client.model;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.items.ItemSonic;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CoralSonicGeoModel extends GeoModel<ItemSonic> {

    private static final ResourceLocation MODEL = AitAPI.modLoc("geo/items/coral.geo.json");
    private static final ResourceLocation TEXTURE = AitAPI.modLoc("textures/item/sonic_tools/coral.png");
    private static final ResourceLocation ANIMATION = AitAPI.modLoc("animations/items/coral.animation.json");

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getModelResource(ItemSonic animatable) {
        return MODEL;
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getTextureResource(ItemSonic animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ItemSonic animatable) {
        return ANIMATION;
    }
}

