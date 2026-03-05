package dev.amble.ait.mixin.accessor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PotionBrewing.class)
public interface AccessorPotionBrewing {
    @Invoker("addMix")
    static void addMix(Potion p1, Item p2, Potion p3) {
    }
}