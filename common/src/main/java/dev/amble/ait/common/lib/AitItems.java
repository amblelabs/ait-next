package dev.amble.ait.common.lib;

import com.google.common.base.Suppliers;
import dev.amble.ait.common.items.ItemScrewdriver;
import dev.amble.ait.common.items.ItemSonic;
import dev.amble.ait.common.items.components.SonicCrystals;
import dev.amble.ait.common.items.components.SonicData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static dev.amble.ait.api.AitAPI.modLoc;

// https://github.com/VazkiiMods/Botania/blob/2c4f7fdf9ebf0c0afa1406dfe1322841133d75fa/Common/src/main/java/vazkii/botania/common/item/ModItems.java
public class AitItems {
    public static void registerItems(BiConsumer<Item, ResourceLocation> r) {
        for (var e : ITEMS.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    public static void registerItemCreativeTab(CreativeModeTab.Output r, CreativeModeTab tab) {
        for (var item : ITEM_TABS.getOrDefault(tab, List.of())) {
            item.register(r);
        }
    }

    private static final Map<ResourceLocation, Item> ITEMS = new LinkedHashMap<>(); // preserve insertion order
    private static final Map<CreativeModeTab, List<TabEntry>> ITEM_TABS = new LinkedHashMap<>();

    public static final Item SCREWDRIVER = make("screwdriver", new ItemScrewdriver(unstackable()));
    public static final Item SONIC_SCREWDRIVER = make("sonic_screwdriver", new ItemSonic(unstackable()
            .component(AitComponents.SONIC_CRYSTALS, SonicCrystals.EMPTY)
            .component(AitComponents.SONIC_DATA, SonicData.DEFAULT)));

    public static final Item SHARD_BASIC = make("zeiton_shard/basic", new Item(unstackable()));
    public static final Item SHARD_OVERCHARGED = make("zeiton_shard/overcharged", new Item(unstackable()));
    public static final Item SHARD_RESONATING = make("zeiton_shard/resonating", new Item(unstackable()));
    public static final Item SHARD_GRAVITY = make("zeiton_shard/gravity", new Item(unstackable()));
    public static final Item SHARD_REFRACTION = make("zeiton_shard/refraction", new Item(unstackable()));
    public static final Item SHARD_AMETHYST = make("zeiton_shard/amethyst", new Item(unstackable()));
    public static final Item SHARD_QUARTZ = make("zeiton_shard/quartz", new Item(unstackable()));
    public static final Item SHARD_SCULK = make("zeiton_shard/sculk", new Item(unstackable()));

    public static Item.Properties props() {
        return new Item.Properties();
    }

    public static Item.Properties unstackable() {
        return props().stacksTo(1);
    }

    private static <T extends Item> T make(ResourceLocation id, T item, @Nullable CreativeModeTab tab) {
        var old = ITEMS.put(id, item);
        if (old != null) throw new IllegalArgumentException("Duplicate id " + id);

        if (tab != null) ITEM_TABS.computeIfAbsent(tab, t -> new ArrayList<>())
                .add(new TabEntry.ItemEntry(item));

        return item;
    }

    private static <T extends Item> T make(String id, T item, @Nullable CreativeModeTab tab) {
        return make(modLoc(id), item, tab);
    }

    private static <T extends Item> T make(String id, T item) {
        return make(id, item, AitCreativeTabs.AIT);
    }

    private static Supplier<ItemStack> addToTab(Supplier<ItemStack> stack, CreativeModeTab tab) {
        var memoised = Suppliers.memoize(stack::get);
        ITEM_TABS.computeIfAbsent(tab, t -> new ArrayList<>()).add(new TabEntry.StackEntry(memoised));
        return memoised;
    }

    private static abstract class TabEntry {
        abstract void register(CreativeModeTab.Output r);

        static class ItemEntry extends TabEntry {
            private final Item item;

            ItemEntry(Item item) {
                this.item = item;
            }

            @Override
            void register(CreativeModeTab.Output r) {
                r.accept(item);
            }
        }

        static class StackEntry extends TabEntry {
            private final Supplier<ItemStack> stack;

            StackEntry(Supplier<ItemStack> stack) {
                this.stack = stack;
            }

            @Override
            void register(CreativeModeTab.Output r) {
                r.accept(stack.get());
            }
        }
    }
}