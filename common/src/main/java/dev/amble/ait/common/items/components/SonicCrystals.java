package dev.amble.ait.common.items.components;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record SonicCrystals(List<ItemStack> items, Fraction weight) implements TooltipComponent {
    
    public static final Codec<SonicCrystals> CODEC = ItemStack.CODEC.listOf().xmap(SonicCrystals::new, SonicCrystals::items);
    public static final StreamCodec<RegistryFriendlyByteBuf, SonicCrystals> STREAM_CODEC = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).map(SonicCrystals::new, SonicCrystals::items);

    public static final SonicCrystals EMPTY = new SonicCrystals(List.of());

    public SonicCrystals(List<ItemStack> list) {
        this(list, computeContentWeight(list));
    }

    private static Fraction computeContentWeight(List<ItemStack> list) {
        Fraction fraction = Fraction.ZERO;

        for(ItemStack itemstack : list) {
            fraction = fraction.add(getWeight(itemstack).multiplyBy(Fraction.getFraction(itemstack.getCount(), 1)));
        }

        return fraction;
    }

    static Fraction getWeight(ItemStack arg) {
        return Fraction.ONE;
    }

    public ItemStack getItemUnsafe(int i) {
        return this.items.get(i);
    }

    public Stream<ItemStack> itemCopyStream() {
        return this.items.stream().map(ItemStack::copy);
    }

    public Iterable<ItemStack> itemsCopy() {
        return Lists.transform(this.items, ItemStack::copy);
    }

    public int size() {
        return this.items.size();
    }

    public Fraction weight() {
        return this.weight;
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (object instanceof SonicCrystals(List<ItemStack> items1, Fraction weight1))
            return this.weight.equals(weight1) && ItemStack.listMatches(this.items, items1);

        return false;
    }

    @Override
    public int hashCode() {
        return ItemStack.hashStackList(this.items);
    }

    @Override
    public @NotNull String toString() {
        return "SonicCrystals" + this.items;
    }

    public static class Mutable {
        private final List<ItemStack> items;
        private Fraction weight;

        public Mutable(SonicCrystals arg) {
            this.items = new ArrayList<>(arg.items);
            this.weight = arg.weight;
        }

        public SonicCrystals.Mutable clearItems() {
            this.items.clear();
            this.weight = Fraction.ZERO;
            return this;
        }

        private int getMaxAmountToAdd(ItemStack arg) {
            Fraction fraction = Fraction.ONE.subtract(this.weight);
            return Math.max(fraction.divideBy(SonicCrystals.getWeight(arg)).intValue(), 0);
        }

        public int tryInsert(ItemStack arg) {
            if (arg.isEmpty() || !arg.getItem().canFitInsideContainerItems())
                return 0;

            int i = Math.min(arg.getCount(), this.getMaxAmountToAdd(arg));

            if (i == 0) {
                return 0;
            } else {
                this.weight = this.weight.add(SonicCrystals.getWeight(arg).multiplyBy(Fraction.getFraction(i, 1)));
                this.items.addFirst(arg.split(i));

                return i;
            }
        }

        public int tryTransfer(Slot arg, Player arg2) {
            ItemStack itemstack = arg.getItem();
            int i = this.getMaxAmountToAdd(itemstack);
            return this.tryInsert(arg.safeTake(itemstack.getCount(), i, arg2));
        }

        @Nullable
        public ItemStack removeOne() {
            if (this.items.isEmpty())
                return null;

            ItemStack itemstack = this.items.removeFirst().copy();
            this.weight = this.weight.subtract(SonicCrystals.getWeight(itemstack).multiplyBy(Fraction.getFraction(itemstack.getCount(), 1)));

            return itemstack;
        }

        public Fraction weight() {
            return this.weight;
        }

        public SonicCrystals toImmutable() {
            return new SonicCrystals(List.copyOf(this.items), this.weight);
        }
    }
}
