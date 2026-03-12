package dev.amble.ait.client.model;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.blocks.ConsoleBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ConsoleGeoModel extends GeoModel<ConsoleBlockEntity> {

    private static final ResourceLocation MODEL = AitAPI.modLoc("geo/blockentities/renaissance_console.geo.json");
    private static final ResourceLocation ANIMATION = AitAPI.modLoc("animations/blockentities/renaissance_console.animation.json");
    private static final ResourceLocation TEXTURE = AitAPI.modLoc("textures/blockentities/consoles/renaissance_default.png");

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getModelResource(ConsoleBlockEntity entity) {
        return MODEL;
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getTextureResource(ConsoleBlockEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ConsoleBlockEntity entity) {
        return ANIMATION;
    }
}

