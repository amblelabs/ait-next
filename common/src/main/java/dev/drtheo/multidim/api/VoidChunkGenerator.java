package dev.drtheo.multidim.api;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.drtheo.multidim.impl.AbstractChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class VoidChunkGenerator extends AbstractChunkGenerator {

    public static final MapCodec<VoidChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.CODEC.fieldOf("biome").forGetter(generator -> generator.biome)
    ).apply(instance, VoidChunkGenerator::new));

    private final Holder<Biome> biome;

    public VoidChunkGenerator(Holder<Biome> biome) {
        super(new FixedBiomeSource(biome));
        this.biome = biome;
    }

    public VoidChunkGenerator(Registry<Biome> biomeRegistry) {
        this(biomeRegistry, Biomes.THE_VOID);
    }

    public VoidChunkGenerator(Registry<Biome> biomeRegistry, net.minecraft.resources.ResourceKey<Biome> biome) {
        this(biomeRegistry.getHolderOrThrow(biome));
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel world, HolderSet<Structure> structures, BlockPos center, int radius, boolean skipReferencedStructures) {
        return null;
    }

    @Override
    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<Biome> biome, StructureManager structureManager, MobCategory group, BlockPos pos) {
        return WeightedRandomList.create();
    }
}


