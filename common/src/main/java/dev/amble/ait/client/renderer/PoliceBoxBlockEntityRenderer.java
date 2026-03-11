package dev.amble.ait.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.amble.ait.api.AitAPI;
import dev.amble.ait.client.model.PoliceBoxModel;
import dev.amble.ait.common.blocks.PoliceBoxBlock;
import dev.amble.ait.common.blocks.PoliceBoxBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PoliceBoxBlockEntityRenderer implements BlockEntityRenderer<PoliceBoxBlockEntity> {

    private static final int FULLBRIGHT = 0xF000F0;

    private static final ResourceLocation[] TEXTURES = {
            AitAPI.modLoc("textures/blockentities/exteriors/police_box_default.png"),
            AitAPI.modLoc("textures/blockentities/exteriors/police_box_coral.png"),
            AitAPI.modLoc("textures/blockentities/exteriors/police_box_renaissance.png"),
            AitAPI.modLoc("textures/blockentities/exteriors/police_box_crystalline.png"),
            AitAPI.modLoc("textures/blockentities/exteriors/police_box_future.png"),
    };

    private static final ResourceLocation[] EMISSION_TEXTURES = new ResourceLocation[TEXTURES.length];

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            String path = TEXTURES[i].getPath();
            String emissivePath = path.replace(".png", "_e.png");
            EMISSION_TEXTURES[i] = AitAPI.modLoc(emissivePath);
        }
    }

    private final PoliceBoxModel<?> model;

    public PoliceBoxBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new PoliceBoxModel<>(context.bakeLayer(PoliceBoxModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoliceBoxBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        int index = blockEntity.getTextureIndex() % TEXTURES.length;
        ResourceLocation texture = TEXTURES[index];
        float alpha = blockEntity.getAlpha();

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.scale(-1f, -1f, 1f);

        int rotation = blockEntity.getBlockState().getValue(PoliceBoxBlock.ROTATION);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation * 45f));

        int packedColor = ((int) (alpha * 255) << 24) | 0xFFFFFF;

        if (alpha < 1.0f) {
            VertexConsumer depthConsumer = bufferSource.getBuffer(AITRenderLayers.tardisDepth(texture));
            model.renderToBuffer(poseStack, depthConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
        }

        VertexConsumer consumer = bufferSource.getBuffer(AITRenderLayers.tardisTranslucent(texture));
        model.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, packedColor);

        ResourceLocation emissionTexture = EMISSION_TEXTURES[index];
        if (Minecraft.getInstance().getResourceManager().getResource(emissionTexture).isPresent()) {
            VertexConsumer emissiveConsumer = bufferSource.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(emissionTexture, true));
            model.renderToBuffer(poseStack, emissiveConsumer, FULLBRIGHT, packedOverlay, packedColor);
        }

        poseStack.popPose();
    }
}

