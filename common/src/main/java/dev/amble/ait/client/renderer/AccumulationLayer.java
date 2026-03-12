package dev.amble.ait.client.renderer;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class AccumulationLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {

    private final ResourceLocation texture;
    private final float textureRes;
    private final float bottomY;
    private final float topY;
    private final float density;
    private final float cutoffVariation;

    public AccumulationLayer(GeoRenderer<T> renderer, ResourceLocation texture, float textureRes) {
        this(renderer, texture, textureRes, 0.0f, 1.5f, 4.0f, 1.2f);
    }

    public AccumulationLayer(GeoRenderer<T> renderer, ResourceLocation texture, float textureRes, float bottomY, float topY) {
        this(renderer, texture, textureRes, bottomY, topY, 4.0f, 1.2f);
    }

    public AccumulationLayer(GeoRenderer<T> renderer, ResourceLocation texture, float textureRes, float bottomY, float topY, float density) {
        this(renderer, texture, textureRes, bottomY, topY, density, 1.2f);
    }

    public AccumulationLayer(GeoRenderer<T> renderer, ResourceLocation texture, float textureRes, float bottomY, float topY, float density, float cutoffVariation) {
        super(renderer);
        this.texture = texture;
        this.textureRes = textureRes;
        this.bottomY = bottomY;
        this.topY = topY;
        this.density = density;
        this.cutoffVariation = cutoffVariation;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource,
                       VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        ShaderInstance shader = AITShaders.getAccumulationShader();
        if (shader == null) return;

        // Extract block position for per-block noise randomisation
        long blockSeed = 0L;
        if (animatable instanceof BlockEntity be) {
            blockSeed = be.getBlockPos().asLong();
        }

        float seedLo = (float)(blockSeed & 0xFFFFL) / 65536.0f;
        float seedHi = (float)((blockSeed >>> 16) & 0xFFFFL) / 65536.0f;

        Matrix4f invModel = new Matrix4f(poseStack.last().pose()).invert();

        Uniform invModelUniform = shader.getUniform("InvModelMat");
        if (invModelUniform != null) invModelUniform.set(invModel);

        Uniform textureResUniform = shader.getUniform("TextureRes");
        if (textureResUniform != null) textureResUniform.set(textureRes);

        Uniform heightUniform = shader.getUniform("HeightParams");
        if (heightUniform != null) heightUniform.set(bottomY, topY);

        Uniform densityUniform = shader.getUniform("Density");
        if (densityUniform != null) densityUniform.set(density);

        Uniform blockSeedUniform = shader.getUniform("BlockSeed");
        if (blockSeedUniform != null) blockSeedUniform.set(seedLo, seedHi);

        Uniform cutoffVarUniform = shader.getUniform("CutoffVariation");
        if (cutoffVarUniform != null) cutoffVarUniform.set(cutoffVariation);

        // Bind the model's own texture as Sampler1 so the shader can skip invisible pixels
        ResourceLocation modelTexture = getGeoModel().getTextureResource(animatable, getRenderer());
        AbstractTexture modelTex = Minecraft.getInstance().getTextureManager().getTexture(modelTexture);
        shader.setSampler("Sampler1", modelTex);

        RenderType accumulationType = AITRenderLayers.accumulation(texture);
        VertexConsumer accumulationConsumer = bufferSource.getBuffer(accumulationType);

        for (GeoBone bone : bakedModel.topLevelBones()) {
            getRenderer().renderRecursively(poseStack, animatable, bone, accumulationType, bufferSource,
                    accumulationConsumer, true, partialTick, packedLight, packedOverlay, 0xFFFFFFFF);
        }

        if (bufferSource instanceof MultiBufferSource.BufferSource immediate) {
            immediate.endBatch(accumulationType);
        }
    }
}
