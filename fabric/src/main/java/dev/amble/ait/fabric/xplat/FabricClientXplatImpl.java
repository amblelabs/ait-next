package dev.amble.liquor.fabric.xplat;

import dev.amble.liquor.common.network.NetworkMessage;
import dev.amble.liquor.fabric.interop.trinkets.TrinketsApiInterop;
import dev.amble.liquor.interop.LiquorInterop;
import dev.amble.liquor.xplat.IClientXplatAbstractions;
import dev.amble.liquor.xplat.IXplatAbstractions;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class FabricClientXplatImpl implements IClientXplatAbstractions {

    @Override
    public void sendPacketToServer(NetworkMessage packet) {
        ClientPlayNetworking.send(packet.getFabricId(), packet.toBuf());
    }

    @Override
    public void setRenderLayer(Block block, RenderType type) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, type);
    }

    @Override
    public void initPlatformSpecific() {
        if (IXplatAbstractions.INSTANCE.isModPresent(LiquorInterop.Fabric.TRINKETS_API_ID)) {
            TrinketsApiInterop.clientInit();
        }
    }

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> type,
                                                          EntityRendererProvider<T> renderer) {
        EntityRendererRegistry.register(type, renderer);
    }

    // suck it fabric trying to be "safe"
    private record UnclampedClampedItemPropFunc(ItemPropertyFunction inner) implements ClampedItemPropertyFunction {
        @Override
        public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity,
                                   int seed) {
            return inner.call(stack, level, entity, seed);
        }

        @Override
        public float call(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            return this.unclampedCall(stack, level, entity, seed);
        }
    }

    @Override
    public void registerItemProperty(Item item, ResourceLocation id, ItemPropertyFunction func) {
        ItemProperties.register(item, id, new UnclampedClampedItemPropFunc(func));
    }
}
