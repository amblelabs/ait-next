package dev.amble.ait.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.items.ItemSonic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CoralSonicEmissiveLayer extends GeoRenderLayer<ItemSonic> {

    private static final ResourceLocation GLOWMASK = AitAPI.modLoc("textures/item/sonic_tools/coral_glowmask.png");
    private static final int FULLBRIGHT = 0xF000F0;

    public CoralSonicEmissiveLayer(GeoRenderer<ItemSonic> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, ItemSonic animatable, BakedGeoModel bakedModel,
                       @Nullable RenderType renderType, MultiBufferSource bufferSource,
                       @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (!player.isUsingItem()) return;

        ItemStack usingItem = player.getUseItem();
        if (!(usingItem.getItem() instanceof ItemSonic)) return;

        if (Minecraft.getInstance().getResourceManager().getResource(GLOWMASK).isEmpty()) return;

        RenderType emissiveType = RenderType.eyes(GLOWMASK);
        VertexConsumer emissiveConsumer = bufferSource.getBuffer(emissiveType);

        for (GeoBone bone : bakedModel.topLevelBones()) {
            getRenderer().renderRecursively(poseStack, animatable, bone, emissiveType, bufferSource,
                    emissiveConsumer, true, partialTick, FULLBRIGHT, packedOverlay, 0xFFFFFFFF);
        }
    }
}
