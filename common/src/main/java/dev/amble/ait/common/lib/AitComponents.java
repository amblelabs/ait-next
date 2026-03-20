package dev.amble.ait.common.lib;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.items.ItemKeychain;
import dev.amble.ait.common.components.ArtronItemData;
import dev.amble.ait.common.components.SonicCrystals;
import dev.amble.ait.common.components.SonicData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

public class AitComponents {

    public static void registerComponents(BiConsumer<DataComponentType<?>, ResourceLocation> r) {
        for (var e : COMPONENTS.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    private static final Map<ResourceLocation, DataComponentType<?>> COMPONENTS = new LinkedHashMap<>();

    public static final DataComponentType<SonicCrystals> SONIC_CRYSTALS = make("sonic_crystals",
            builder -> builder.persistent(SonicCrystals.CODEC)
                    .networkSynchronized(SonicCrystals.STREAM_CODEC).cacheEncoding());

    public static final DataComponentType<SonicData> SONIC = make("sonic",
            builder -> builder.persistent(SonicData.CODEC)
                    .networkSynchronized(SonicData.STREAM_CODEC).cacheEncoding());

    public static final DataComponentType<ArtronItemData> ARTRON = make("artron",
            builder -> builder.persistent(ArtronItemData.CODEC)
                    .networkSynchronized(ArtronItemData.STREAM_CODEC).cacheEncoding());

    public static final DataComponentType<ItemKeychain.KeychainContents> KEYCHAIN_CONTENTS = make("keychain_contents",
            builder -> builder.persistent(ItemKeychain.KeychainContents.CODEC)
                    .networkSynchronized(ItemKeychain.KeychainContents.STREAM_CODEC)
                    .cacheEncoding());

    public static final FoodProperties LIGHTBULB_FOOD_COMPONENT = new FoodProperties.Builder()
            .nutrition(5)
            .saturationModifier(0.6f)
            .fast()
            .build();

    private static <T> DataComponentType<T> make(String name, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        return make(AitAPI.modLoc(name), unaryOperator);
    }

    private static <T> DataComponentType<T> make(ResourceLocation loc, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        DataComponentType<T> type = unaryOperator.apply(DataComponentType.builder()).build();
        COMPONENTS.put(loc, type);

        return type;
    }
}