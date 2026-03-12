package dev.amble.ait.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.amble.ait.client.model.DoorGeoModel;
import dev.amble.ait.common.blocks.DoorBlock;
import dev.amble.ait.common.blocks.DoorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DoorBlockEntityRenderer extends GeoBlockRenderer<DoorBlockEntity> {

    public DoorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new DoorGeoModel());
        addRenderLayer(new DoorEmissiveLayer(this));
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
    }

    @Override
    public void preRender(PoseStack poseStack, DoorBlockEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          int colour) {
        if (!isReRender) {
            int rotation = entity.getBlockState().getValue(DoorBlock.ROTATION);
            boolean onSlab = entity.isOnSlab();
            boolean between = entity.isBetween();

            double xOff = 0.5;
            double yOff = onSlab ? -0.5 : 0;
            double zOff = 0.5;

            if (between) {
                double[][] offsets = DoorBlock.BETWEEN_OFFSETS;
                xOff += offsets[rotation][0];
                zOff += offsets[rotation][2];
            }

            poseStack.translate(xOff, yOff, zOff);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation * -45f));
        }
    }
}
