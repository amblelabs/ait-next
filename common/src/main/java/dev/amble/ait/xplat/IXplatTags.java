package dev.amble.ait.xplat;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

// https://fabricmc.net/wiki/tutorial:tags#existing_common_tags
public interface IXplatTags {
    TagKey<Item> bottleable();
    TagKey<Item> drinks();

    TagKey<Item> cactusJuiceLike();
    TagKey<Item> honeyLike();
    TagKey<Item> potionLike();
    TagKey<Item> milkLike();
    TagKey<Item> ominousLike();
    TagKey<Item> sodaLike();
    TagKey<Item> teaLike();

    TagKey<Item> water();
    TagKey<Item> waterLike();
}