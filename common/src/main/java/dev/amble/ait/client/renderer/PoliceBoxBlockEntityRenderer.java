package dev.amble.ait.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.amble.ait.client.model.PoliceBoxGeoModel;
import dev.amble.ait.common.blocks.PoliceBoxBlock;
import dev.amble.ait.common.blocks.PoliceBoxBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PoliceBoxBlockEntityRenderer extends GeoBlockRenderer<PoliceBoxBlockEntity> {

    public PoliceBoxBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new PoliceBoxGeoModel());
        addRenderLayer(new PoliceBoxEmissiveLayer(this));
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        // No-op: we handle rotation via ROTATION blockstate in preRender
    }

    @Override
    public RenderType getRenderType(PoliceBoxBlockEntity entity, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return AITRenderLayers.tardisTranslucent(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, PoliceBoxBlockEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          int colour) {
        if (!isReRender) {
            int rotation = entity.getBlockState().getValue(PoliceBoxBlock.ROTATION);
            poseStack.translate(0.5,0,0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation * -45f));
        }
    }

    @Override
    public void actuallyRender(PoseStack poseStack, PoliceBoxBlockEntity entity, BakedGeoModel model,
                               RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                               boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                               int colour) {
        float alpha = 0.1f;//entity.getAlpha();
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
}
