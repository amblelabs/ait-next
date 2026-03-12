package dev.amble.ait.fabric.xplat;

import com.google.common.base.Suppliers;
import dev.amble.ait.api.AitAPI;
import dev.amble.ait.api.mod.AitTags;
import dev.amble.ait.fabric.interop.trinkets.TrinketsApiInterop;
import dev.amble.ait.interop.AitInterop;
import dev.amble.ait.xplat.IXplatAbstractions;
import dev.amble.ait.xplat.IXplatTags;
import dev.amble.ait.xplat.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class FabricXplatImpl implements IXplatAbstractions {

    @Override
    public Platform platform() {
        return Platform.FABRIC;
    }

    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean isModPresent(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public void initPlatformSpecific() {
        if (this.isModPresent(AitInterop.Fabric.TRINKETS_API_ID)) {
            TrinketsApiInterop.init();
        }
    }

    @Override
    public void sendPacketToPlayer(ServerPlayer target, CustomPacketPayload packet) {
        ServerPlayNetworking.send(target, packet);
    }

    @Override
    public void sendPacketNear(Vec3 pos, double radius, ServerLevel dimension, CustomPacketPayload packet) {
        sendPacketToPlayers(PlayerLookup.around(dimension, pos, radius), packet);
    }

    @Override
    public void sendPacketTracking(Entity entity, CustomPacketPayload packet) {
        sendPacketToPlayers(PlayerLookup.tracking(entity), packet);
    }

    private void sendPacketToPlayers(Collection<ServerPlayer> players, CustomPacketPayload packet) {
        Packet<?> pkt = this.toVanilla(packet);

        for (var p : players) {
            p.connection.send(pkt);
        }
    }

    @Override
    public Packet<ClientCommonPacketListener> toVanilla(CustomPacketPayload message) {
        return ServerPlayNetworking.createS2CPacket(message);
    }

    @Override
    @SuppressWarnings("deprecation")
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(func::apply, blocks).build();
    }

    @Override
    public boolean tryPlaceFluid(Level level, InteractionHand hand, BlockPos pos, Fluid fluid) {
        Storage<FluidVariant> target = FluidStorage.SIDED.find(level, pos, Direction.UP);
        if (target == null) {
            return false;
        }
        try (Transaction transaction = Transaction.openOuter()) {
            long insertedAmount = target.insert(FluidVariant.of(fluid), FluidConstants.BUCKET, transaction);
            if (insertedAmount > 0) {
                transaction.commit();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean drainAllFluid(Level level, BlockPos pos) {
        Storage<FluidVariant> target = FluidStorage.SIDED.find(level, pos, Direction.UP);
        if (target == null) {
            return false;
        }
        try (Transaction transaction = Transaction.openOuter()) {
            boolean any = false;
            for (var view : target) {
                long extracted = view.extract(view.getResource(), view.getAmount(), transaction);
                if (extracted > 0) {
                    any = true;
                }
            }

            if (any) {
                transaction.commit();
                return true;
            }
        }
        return false;
    }

    private static final IXplatTags TAGS = new IXplatTags() {

    };

    @Override
    public IXplatTags tags() {
        return TAGS;
    }

    @Override
    public LootItemCondition.Builder isShearsCondition() {
        return AnyOfCondition.anyOf(
            MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS)),
            MatchTool.toolMatches(ItemPredicate.Builder.item().of(
                AitTags.Items.create(ResourceLocation.fromNamespaceAndPath("c", "shears"))))
        );
    }

    @Override
    public String getModName(String namespace) {
        if (namespace.equals("c")) return "Common";

        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(namespace);
        if (container.isPresent()) return container.get().getMetadata().getName();

        return namespace;
    }

    @Override
    public boolean isDev() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isUnstable() {
        return UNSTABLE.get();
    }

    private static final Supplier<Boolean> UNSTABLE = Suppliers.memoize(() ->
            !FabricLoader.getInstance().getModContainer(AitAPI.MOD_ID)
                    .orElseThrow().getMetadata().getVersion()
                    .getFriendlyString().contains("release"));

    //    private static final Supplier<Registry<>>  = Suppliers.memoize(() ->
//        FabricRegistryBuilder.from(new DefaultedMappedRegistry<>(
//                LiquorAPI.MOD_ID + ":nothing", LiquorRegistries.,
//                Lifecycle.stable(), false))
//            .buildAndRegister()
//    );
}