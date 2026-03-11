package dev.amble.ait.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AITRenderLayers extends RenderType {

    public AITRenderLayers(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
    }

    private static final Function<ResourceLocation, RenderType> TARDIS_DEPTH = Util
            .memoize((texture) -> {
                CompositeState compositeState = CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setCullState(CULL)
                        .setTransparencyState(NO_TRANSPARENCY)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setWriteMaskState(DEPTH_WRITE)
                        .createCompositeState(false);
                return new CompositeRenderType("tardis_depth",
                        DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                        false, false, compositeState);
            });

    public static RenderType tardisDepth(ResourceLocation texture) {
        return TARDIS_DEPTH.apply(texture);
    }

    private static final Function<ResourceLocation, RenderType> TARDIS_TRANSLUCENT = Util
            .memoize((texture) -> {
                CompositeState compositeState = CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setCullState(CULL)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .createCompositeState(true);
                return new CompositeRenderType("tardis_translucent",
                        DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                        true, true, compositeState);
            });

    public static RenderType tardisTranslucent(ResourceLocation texture) {
        return TARDIS_TRANSLUCENT.apply(texture);
    }

    private static final BiFunction<ResourceLocation, Boolean, RenderType> EMISSIVE_CULL_Z_OFFSET = Util
            .memoize((texture, affectsOutline) -> {
                CompositeState compositeState = CompositeState.builder()
                        .setShaderState(RENDERTYPE_EYES_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setCullState(CULL)
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
