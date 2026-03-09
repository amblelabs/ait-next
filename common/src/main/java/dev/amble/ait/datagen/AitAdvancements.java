package dev.amble.ait.datagen;

import dev.amble.ait.api.AitAPI;
import dev.amble.lib.datagen.AmbleAdvancementSubProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;

import java.util.function.Consumer;

public class AitAdvancements extends AmbleAdvancementSubProvider {

    public AitAdvancements() {
        super(AitAPI.MOD_ID);
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer) { }
}