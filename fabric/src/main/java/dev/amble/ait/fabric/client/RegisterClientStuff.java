package dev.amble.ait.fabric.client;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.client.renderer.ClientSonicTooltip;
import dev.amble.ait.common.items.ItemSonic;
import dev.amble.ait.common.items.components.SonicCrystals;
import dev.amble.ait.common.items.tooltips.SonicTooltip;
import dev.amble.ait.common.lib.AitItems;
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

        x.registerItemProperty(AitItems.SONIC_SCREWDRIVER, AitAPI.modLoc("sonic_crystal"),
                (itemStack, clientLevel, livingEntity, i) -> ItemSonic.getCrystal(itemStack));
    }

    public static void registerColorProviders(BiConsumer<ItemColor, Item> itemColorRegistry,
                                              BiConsumer<BlockColor, Block> blockColorRegistry) {

    }

    public static void registerBlockEntityRenderers(@NotNull BlockEntityRendererRegisterer registerer) {

    }

    @FunctionalInterface
    public interface BlockEntityRendererRegisterer {
        <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type,
            BlockEntityRendererProvider<? super T> berp);
    }
}