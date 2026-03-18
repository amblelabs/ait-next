package dev.amble.ait.fabric.multidim;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.amble.lib.multidim.MultiDim;
import dev.amble.lib.multidim.api.MultiDimServerLevel;
import dev.amble.lib.multidim.api.VoidChunkGenerator;
import dev.amble.lib.multidim.api.WorldBlueprint;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

public final class AitMultiDimDemo {

    public static final ResourceLocation DEMO_BLUEPRINT_ID = ResourceLocation.fromNamespaceAndPath("ait", "void_demo");
    public static final ResourceLocation DEMO_LEVEL_ID = ResourceLocation.fromNamespaceAndPath("ait", "demo_void");
    public static final ResourceLocation DEMO_CHUNK_GENERATOR_ID = ResourceLocation.fromNamespaceAndPath("ait", "void");
    public static final ResourceKey<Level> DEMO_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, DEMO_LEVEL_ID);

    private static boolean initialized;

    private AitMultiDimDemo() {
    }

    public static void init() {
        if (initialized) {
            return;
        }

        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, DEMO_CHUNK_GENERATOR_ID, VoidChunkGenerator.CODEC);
        ServerLifecycleEvents.SERVER_STARTING.register(AitMultiDimDemo::registerBlueprints);
        initialized = true;
    }

    public static void registerCommands(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("multidim")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("demo")
                        .then(Commands.literal("info").executes(context -> info(context.getSource())))
                        .then(Commands.literal("create").executes(context -> create(context.getSource())))
                        .then(Commands.literal("tp").executes(context -> teleport(context.getSource())))
                        .then(Commands.literal("unload").executes(context -> unload(context.getSource())))
                        .then(Commands.literal("remove").executes(context -> remove(context.getSource())))));
    }

    private static void registerBlueprints(MinecraftServer server) {
        ensureDemoBlueprint(server);
    }

    private static WorldBlueprint ensureDemoBlueprint(MinecraftServer server) {
        MultiDim multiDim = MultiDim.get(server);
        WorldBlueprint existing = multiDim.getBlueprint(DEMO_BLUEPRINT_ID);

        if (existing != null) {
            return existing;
        }

        Registry<net.minecraft.world.level.biome.Biome> biomeRegistry = server.registryAccess().registryOrThrow(Registries.BIOME);
        WorldBlueprint blueprint = new WorldBlueprint(DEMO_BLUEPRINT_ID)
                .withType(BuiltinDimensionTypes.OVERWORLD.location())
                .withGenerator(new VoidChunkGenerator(biomeRegistry))
                .withSeed(0L)
                .shouldTickTime(false)
                .setPersistent(true)
                .setAutoLoad(true);

        multiDim.register(blueprint);
        return blueprint;
    }

    private static ServerLevel ensureDemoLevel(MinecraftServer server) {
        ServerLevel existing = server.getLevel(DEMO_LEVEL_KEY);
        if (existing != null) {
            ensureSpawnPlatform(existing);
            return existing;
        }

        MultiDim multiDim = MultiDim.get(server);
        WorldBlueprint blueprint = ensureDemoBlueprint(server);
        MultiDimServerLevel level = multiDim.addOrLoad(blueprint, DEMO_LEVEL_KEY, true);
        ensureSpawnPlatform(level);
        return level;
    }

    private static void ensureSpawnPlatform(ServerLevel level) {
        BlockPos spawn = level.getSharedSpawnPos();
        BlockPos floorCenter = spawn.below();

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos pos = floorCenter.offset(x, 0, z);
                level.setBlock(pos, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
            }
        }

        level.setBlock(floorCenter.above(), Blocks.TORCH.defaultBlockState(), Block.UPDATE_ALL);
    }

    private static int info(CommandSourceStack source) {
        ensureDemoBlueprint(source.getServer());
        ServerLevel level = source.getServer().getLevel(DEMO_LEVEL_KEY);
        boolean loaded = level != null;
        String levelType = loaded ? level.getClass().getSimpleName() : "none";

        source.sendSuccess(() -> Component.literal("AIT multidim demo -> blueprint=" + DEMO_BLUEPRINT_ID + ", level=" + DEMO_LEVEL_ID + ", loaded=" + loaded + ", type=" + levelType), false);
        return loaded ? 1 : 0;
    }

    private static int create(CommandSourceStack source) {
        ServerLevel level = ensureDemoLevel(source.getServer());
        source.sendSuccess(() -> Component.literal("Created/loaded demo dimension " + level.dimension().location() + " (" + level.getClass().getSimpleName() + ")"), true);
        return 1;
    }

    private static int teleport(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = ensureDemoLevel(source.getServer());
        BlockPos spawn = level.getSharedSpawnPos();

        player.teleportTo(level, spawn.getX() + 0.5D, spawn.getY() + 1.0D, spawn.getZ() + 0.5D, player.getYRot(), player.getXRot());
        source.sendSuccess(() -> Component.literal("Teleported to demo dimension " + level.dimension().location()), false);
        return 1;
    }

    private static int unload(CommandSourceStack source) {
        ServerLevel level = source.getServer().getLevel(DEMO_LEVEL_KEY);
        if (level == null) {
            source.sendFailure(Component.literal("Demo dimension is not currently loaded."));
            return 0;
        }

        MultiDim.get(source.getServer()).queueUnload(DEMO_LEVEL_KEY);
        source.sendSuccess(() -> Component.literal("Queued unload for demo dimension " + DEMO_LEVEL_ID), true);
        return 1;
    }

    private static int remove(CommandSourceStack source) {
        ServerLevel level = source.getServer().getLevel(DEMO_LEVEL_KEY);
        if (level == null) {
            source.sendFailure(Component.literal("Demo dimension is not currently loaded."));
            return 0;
        }

        MultiDim.get(source.getServer()).queueRemove(DEMO_LEVEL_KEY);
        source.sendSuccess(() -> Component.literal("Queued removal for demo dimension " + DEMO_LEVEL_ID), true);
        return 1;
    }
}




