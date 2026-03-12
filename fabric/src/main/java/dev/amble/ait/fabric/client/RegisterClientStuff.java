package dev.amble.ait.fabric.client;

import dev.amble.ait.client.renderer.DoorBlockEntityRenderer;
import dev.amble.ait.client.renderer.FallingTardisBlockRenderer;
import dev.amble.ait.client.renderer.PoliceBoxBlockEntityRenderer;
import dev.amble.ait.client.renderer.ClientSonicTooltip;
import dev.amble.ait.common.items.components.SonicCrystals;
import dev.amble.ait.common.items.tooltips.SonicTooltip;
import dev.amble.ait.common.lib.AitBlockEntities;
import dev.amble.ait.common.lib.AitEntities;
import dev.amble.ait.xplat.IClientXplatAbstractions;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class RegisterClientStuff {

    public static void init() {
        TooltipComponentCallback.EVENT.register(component -> {
            if (component instanceof SonicTooltip(SonicCrystals contents))
                return new ClientSonicTooltip(contents);

            return null;
        });

        var x = IClientXplatAbstractions.INSTANCE;
        x.registerEntityRenderer(AitEntities.FALLING_TARDIS_BLOCK, FallingTardisBlockRenderer::new);

//        x.registerItemProperty(AitItems.SONIC_SCREWDRIVER, AitAPI.modLoc("sonic_crystal"),
//                (itemStack, clientLevel, livingEntity, i) -> ItemSonic.getCrystal(itemStack));
    }

    public static void registerColorProviders(BiConsumer<ItemColor, Item> itemColorRegistry,
                                              BiConsumer<BlockColor, Block> blockColorRegistry) {

    }

    public static void registerBlockEntityRenderers(@NotNull BlockEntityRendererRegisterer registerer) {
        registerer.registerBlockEntityRenderer(AitBlockEntities.POLICE_BOX_BLOCK_ENTITY, PoliceBoxBlockEntityRenderer::new);
        registerer.registerBlockEntityRenderer(AitBlockEntities.DOOR_BLOCK_ENTITY, DoorBlockEntityRenderer::new);
    }

    @FunctionalInterface
    public interface BlockEntityRendererRegisterer {
        <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type,
            BlockEntityRendererProvider<? super T> berp);
    }
}