package dev.amble.lib.datagen;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public abstract class AmbleAdvancementSubProvider implements AdvancementSubProvider {

    protected final String modId;

    protected AmbleAdvancementSubProvider(String modId) {
        this.modId = modId;
    }

    protected DisplayInfo simple(ItemLike icon, String name, FrameType frameType) {
        return simpleBg(icon, name, frameType, null);
    }

    protected DisplayInfo simpleBg(ItemLike icon, String name, FrameType frameType, @Nullable ResourceLocation background) {
        return display(new ItemStack(icon), name, frameType, background, true, true, false);
    }

    protected DisplayInfo display(ItemStack icon, String name, FrameType frameType, ResourceLocation background, boolean showToast, boolean announceChat, boolean hidden) {
        name = "advancement." + this.modId + ":" + name;

        return new DisplayInfo(icon,
            Component.translatable(name),
            Component.translatable(name + ".desc"),
            background, frameType, showToast, announceChat, hidden);
    }

    protected ResourceLocation modLoc(String name) {
        return new ResourceLocation(modId, name);
    }
}