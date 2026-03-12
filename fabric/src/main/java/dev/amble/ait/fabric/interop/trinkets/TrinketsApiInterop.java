package dev.amble.ait.fabric.interop.trinkets;

import com.google.common.collect.Multimap;
import dev.amble.ait.common.items.AitBaubleItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public class TrinketsApiInterop {
    public static void init() {
        BuiltInRegistries.ITEM.stream().forEach(item -> {
            if (item instanceof AitBaubleItem bauble) {
                TrinketsApi.registerTrinket(item, new Trinket() {

                    @Override
                    public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, ResourceLocation slotIdentifier) {
                        var map = Trinket.super.getModifiers(stack, slot, entity, slotIdentifier);
                        map.putAll(bauble.getAttrs(stack));
                        return map;
                    }
                });
            }
        });
    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("EmptyMethod")
    public static void clientInit() {
//        TrinketRendererRegistry.registerRenderer
    }
}
