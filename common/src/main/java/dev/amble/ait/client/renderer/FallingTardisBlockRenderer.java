package dev.amble.ait.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.amble.ait.api.AitAPI;
import dev.amble.ait.client.model.FallingTardisGeoModel;
import dev.amble.ait.common.blocks.FallingTardisBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class FallingTardisBlockRenderer extends GeoEntityRenderer<FallingTardisBlockEntity> {

    public FallingTardisBlockRenderer(EntityRendererProvider.Context context) {
        super(context, new FallingTardisGeoModel());
        addRenderLayer(new FallingTardisEmissiveLayer(this));
    }

    @Override
    protected void applyRotations(FallingTardisBlockEntity animatable, PoseStack poseStack,
                                  float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        // No-op: skip the default 180° Y rotation from GeoEntityRenderer.
        // We handle rotation via the ROTATION blockstate in preRender instead.
    }

    @Override
    public RenderType getRenderType(FallingTardisBlockEntity entity, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return AITRenderLayers.tardisTranslucent(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, FallingTardisBlockEntity entity, BakedGeoModel model,
                          @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          int colour) {
        if (!isReRender) {
            int rotation = entity.getRotation();
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation * -45f));

            int doorState = entity.getDoorStateOrdinal();
            float rightY = (float) Math.toRadians(-77.5);
            float leftY = (float) Math.toRadians(77.5);

            setDoorBone(model, "door_r", doorState >= 1 ? rightY : 0);
            setDoorBone(model, "RDoor", doorState >= 1 ? rightY : 0);
            setDoorBone(model, "door_l", doorState >= 2 ? leftY : 0);
            setDoorBone(model, "LDoor", doorState >= 2 ? leftY : 0);
        }
    }

    @Override
    public void actuallyRender(PoseStack poseStack, FallingTardisBlockEntity entity, BakedGeoModel model,
                               @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                               boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                               int colour) {
        float alpha = entity.getAlpha();
        int packedColor = ((int) (alpha * 255) << 24) | 0xFFFFFF;

        if (alpha < 1.0f && bufferSource instanceof MultiBufferSource.BufferSource immediate) {
            ResourceLocation texture = getGeoModel().getTextureResource(entity, this);

            RenderType depthType = AITRenderLayers.tardisDepth(texture);
            VertexConsumer depthConsumer = bufferSource.getBuffer(depthType);
            super.actuallyRender(poseStack, entity, model, depthType, bufferSource, depthConsumer,
                    isReRender, partialTick, packedLight, packedOverlay, 0xFFFFFFFF);
            immediate.endBatch(depthType);

            VertexConsumer frontConsumer = bufferSource.getBuffer(renderType);
            super.actuallyRender(poseStack, entity, model, renderType, bufferSource, frontConsumer,
                    isReRender, partialTick, packedLight, packedOverlay, packedColor);
            immediate.endBatch(renderType);
        } else {
            super.actuallyRender(poseStack, entity, model, renderType, bufferSource, buffer,
                    isReRender, partialTick, packedLight, packedOverlay, packedColor);
        }
    }

    private static void setDoorBone(BakedGeoModel model, String name, float rotY) {
        model.getBone(name).ifPresent(bone -> bone.setRotY(rotY));
    }

    private static class FallingTardisEmissiveLayer extends GeoRenderLayer<FallingTardisBlockEntity> {

        private static final int FULLBRIGHT = 0xF000F0;

        public FallingTardisEmissiveLayer(GeoRenderer<FallingTardisBlockEntity> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, FallingTardisBlockEntity entity, BakedGeoModel bakedModel,
                           @Nullable RenderType renderType, MultiBufferSource bufferSource,
                           @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            ResourceLocation baseTexture = getGeoModel().getTextureResource(entity, getRenderer());
            String basePath = baseTexture.getPath();
            String emissivePath = basePath.replace(".png", "_e.png");
            ResourceLocation emissiveTexture = AitAPI.modLoc(emissivePath);

            if (Minecraft.getInstance().getResourceManager().getResource(emissiveTexture).isEmpty()) return;

            float alpha = entity.getAlpha();
            int packedColor = ((int) (alpha * 255) << 24) | 0xFFFFFF;

            RenderType emissiveType = AITRenderLayers.tardisEmissiveCullZOffset(emissiveTexture, true);

            if (alpha < 1.0f && bufferSource instanceof MultiBufferSource.BufferSource immediate) {
                RenderType depthType = AITRenderLayers.tardisDepth(emissiveTexture);
                VertexConsumer depthConsumer = bufferSource.getBuffer(depthType);
                for (GeoBone bone : bakedModel.topLevelBones()) {
                    getRenderer().renderRecursively(poseStack, entity, bone, depthType, bufferSource,
                            depthConsumer, true, partialTick, FULLBRIGHT, packedOverlay, 0xFFFFFFFF);
                }
                immediate.endBatch(depthType);

                VertexConsumer emissiveConsumer = bufferSource.getBuffer(emissiveType);
                for (GeoBone bone : bakedModel.topLevelBones()) {
                    getRenderer().renderRecursively(poseStack, entity, bone, emissiveType, bufferSource,
                            emissiveConsumer, true, partialTick, FULLBRIGHT, packedOverlay, packedColor);
                }
                immediate.endBatch(emissiveType);
            } else {
                VertexConsumer emissiveConsumer = bufferSource.getBuffer(emissiveType);
                for (GeoBone bone : bakedModel.topLevelBones()) {
                    getRenderer().renderRecursively(poseStack, entity, bone, emissiveType, bufferSource,
                            emissiveConsumer, true, partialTick, FULLBRIGHT, packedOverlay, packedColor);
                }
            }
        }
    }
}
