package dev.amble.ait.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.amble.ait.client.model.ConsoleGeoModel;
import dev.amble.ait.common.blocks.ConsoleBlock;
import dev.amble.ait.common.blocks.ConsoleBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ConsoleBlockEntityRenderer extends GeoBlockRenderer<ConsoleBlockEntity> {

    public ConsoleBlockEntityRenderer(BlockEntityRendererProvider.Context ignoredContext) {
        super(new ConsoleGeoModel());
        addRenderLayer(new ConsoleEmissiveLayer(this));
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        // No-op: we handle rotation via ROTATION blockstate in preRender
    }

    @Override
    public RenderType getRenderType(ConsoleBlockEntity entity, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return AITRenderLayers.consoleCutoutNoCullZOffset(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, ConsoleBlockEntity entity, BakedGeoModel model,
                          @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          int colour) {
        if (!isReRender) {
            int rotation = entity.getBlockState().getValue(ConsoleBlock.ROTATION);
            boolean onSlab = entity.isOnSlab();
            boolean between = entity.isBetween();
            int betweenCorner = entity.getBlockState().getValue(ConsoleBlock.BETWEEN_CORNER);

            double xOff = 0.5;
            double yOff = onSlab ? -0.5 : 0;
            double zOff = 0.5;

            if (between) {
                double[][] offsets = ConsoleBlock.BETWEEN_OFFSETS;
                xOff += offsets[betweenCorner][0];
                zOff += offsets[betweenCorner][1];
            }

            poseStack.translate(xOff, yOff, zOff);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation * -45f));
        }
    }
}

