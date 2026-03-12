package dev.amble.ait.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.blocks.ConsoleBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class ConsoleEmissiveLayer extends GeoRenderLayer<ConsoleBlockEntity> {

    private static final int FULLBRIGHT = 0xF000F0;

    public ConsoleEmissiveLayer(GeoRenderer<ConsoleBlockEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, ConsoleBlockEntity entity, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource,
                       VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ResourceLocation baseTexture = getGeoModel().getTextureResource(entity, getRenderer());
        String basePath = baseTexture.getPath();
        String emissivePath = basePath.replace(".png", "_e.png");
        ResourceLocation emissiveTexture = AitAPI.modLoc(emissivePath);

        if (Minecraft.getInstance().getResourceManager().getResource(emissiveTexture).isEmpty()) return;

        RenderType emissiveType = AITRenderLayers.tardisEmissiveCullZOffset(emissiveTexture, true);
        VertexConsumer emissiveConsumer = bufferSource.getBuffer(emissiveType);

        for (GeoBone bone : bakedModel.topLevelBones()) {
            getRenderer().renderRecursively(poseStack, entity, bone, emissiveType, bufferSource,
                    emissiveConsumer, true, partialTick, FULLBRIGHT, packedOverlay, 0xFFFFFFFF);
        }
    }
}

