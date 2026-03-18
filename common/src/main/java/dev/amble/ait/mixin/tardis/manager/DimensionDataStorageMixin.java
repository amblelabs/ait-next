package dev.amble.ait.mixin.tardis.manager;

import com.mojang.datafixers.DataFixer;
import dev.amble.ait.api.mod.storage.PlainLazyDirectoryDimensionDataStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.File;

@Mixin(DimensionDataStorage.class)
public class DimensionDataStorageMixin implements PlainLazyDirectoryDimensionDataStorage.Provider {

    @Shadow
    @Final
    private File dataFolder;
    @Shadow
    @Final
    private DataFixer fixerUpper;
    @Shadow
    @Final
    private HolderLookup.Provider registries;

    @Unique
    private @Nullable PlainLazyDirectoryDimensionDataStorage ait$storage;

    @Override
    public PlainLazyDirectoryDimensionDataStorage ait$getOrCreate() {
        if (this.ait$storage != null) return this.ait$storage;
        return this.ait$storage = new PlainLazyDirectoryDimensionDataStorage(this.dataFolder, this.fixerUpper, this.registries);
    }
}
