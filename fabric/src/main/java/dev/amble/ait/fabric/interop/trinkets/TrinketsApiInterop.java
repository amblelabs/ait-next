package dev.amble.liquor.fabric.interop.trinkets;

import com.google.common.collect.Multimap;
import dev.amble.liquor.common.items.LiquorBaubleItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class TrinketsApiInterop {
    public static void init() {
        BuiltInRegistries.ITEM.stream().forEach(item -> {
            if (item instanceof LiquorBaubleItem bauble) {
                TrinketsApi.registerTrinket(item, new Trinket() {
                    @Override
                    public Multimap<Attribute, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot,
                                                                               LivingEntity entity, UUID uuid) {
                        var map = Trinket.super.getModifiers(stack, slot, entity, uuid);
                        map.putAll(bauble.getAttrs(stack));
                        return map;
                    }
                });
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
//        TrinketRendererRegistry.registerRenderer
    }
}
