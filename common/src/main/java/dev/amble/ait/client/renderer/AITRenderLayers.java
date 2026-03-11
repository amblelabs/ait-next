package dev.amble.ait.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

public class AITRenderLayers extends RenderType {

    public AITRenderLayers(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
    }

    private static final BiFunction<ResourceLocation, Boolean, RenderType> EMISSIVE_CULL_Z_OFFSET = Util
            .memoize((texture, affectsOutline) -> {
                CompositeState compositeState = CompositeState.builder()
                        .setShaderState(RENDERTYPE_EYES_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setCullState(NO_CULL)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setLightmapState(LIGHTMAP)
                        .setWriteMaskState(COLOR_WRITE)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .createCompositeState(false);
                return new CompositeRenderType("emissive_cull_z_offset",
                        DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                        false, true, compositeState);
            });

    public static RenderType tardisEmissiveCullZOffset(ResourceLocation texture, boolean affectsOutline) {
        return EMISSIVE_CULL_Z_OFFSET.apply(texture, affectsOutline);
    }
}
