package dev.amble.ait.xplat;

import dev.amble.ait.api.AitAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public interface IXplatAbstractions {
    Platform platform();

    boolean isModPresent(String id);

    String getModName(String namespace);

    boolean isPhysicalClient();

    void initPlatformSpecific();

    void sendPacketToPlayer(ServerPlayer target, CustomPacketPayload packet);

    void sendPacketNear(Vec3 pos, double radius, ServerLevel dimension, CustomPacketPayload packet);

    void sendPacketTracking(Entity entity, CustomPacketPayload packet);

    // https://github.com/VazkiiMods/Botania/blob/13b7bcd9cbb6b1a418b0afe455662d29b46f1a7f/Xplat/src/main/java/vazkii/botania/xplat/IXplatAbstractions.java#L157
    Packet<ClientCommonPacketListener> toVanilla(CustomPacketPayload message);

    // Blocks

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> func,
        Block... blocks);

    boolean tryPlaceFluid(Level level, InteractionHand hand, BlockPos pos, Fluid fluid);

    boolean drainAllFluid(Level level, BlockPos pos);

    // misc

    IXplatTags tags();

    LootItemCondition.Builder isShearsCondition();

    // TODO:
    // Registry<Stuff> getStuffRegistry();

    IXplatAbstractions INSTANCE = find();

    private static IXplatAbstractions find() {
        var providers = ServiceLoader.load(IXplatAbstractions.class).stream().toList();

        if (providers.size() != 1) {
            var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
            throw new IllegalStateException(
                "There should be exactly one IXplatAbstractions implementation on the classpath. Found: " + names);
        } else {
            var provider = providers.get(0);
            AitAPI.LOGGER.debug("Instantiating xplat impl: {}", provider.type().getName());
            return provider.get();
        }
    }
}