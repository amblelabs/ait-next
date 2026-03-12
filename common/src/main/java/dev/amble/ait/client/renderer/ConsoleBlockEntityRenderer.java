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
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ConsoleBlockEntityRenderer extends GeoBlockRenderer<ConsoleBlockEntity> {

    public ConsoleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new ConsoleGeoModel());
        addRenderLayer(new ConsoleEmissiveLayer(this));
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        // No-op: we handle rotation via ROTATION blockstate in preRender
    }

    @Override
    public RenderType getRenderType(ConsoleBlockEntity entity, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, ConsoleBlockEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          int colour) {
        if (!isReRender) {
            int rotation = entity.getBlockState().getValue(ConsoleBlock.ROTATION);
            boolean onSlab = entity.isOnSlab();
            poseStack.translate(0.5, onSlab ? -0.5 : 0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation * -45f));
        }
    }
}

