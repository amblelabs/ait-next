package dev.amble.liquor.fabric.xplat;

import dev.amble.liquor.api.mod.LiquorTags;
import dev.amble.liquor.common.network.NetworkMessage;
import dev.amble.liquor.fabric.interop.trinkets.TrinketsApiInterop;
import dev.amble.liquor.interop.LiquorInterop;
import dev.amble.liquor.xplat.IXplatAbstractions;
import dev.amble.liquor.xplat.IXplatTags;
import dev.amble.liquor.xplat.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
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
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
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
        if (this.isModPresent(LiquorInterop.Fabric.TRINKETS_API_ID)) {
            TrinketsApiInterop.init();
        }
    }

    @Override
    public void sendPacketToPlayer(ServerPlayer target, NetworkMessage packet) {
        ServerPlayNetworking.send(target, packet.getFabricId(), packet.toBuf());
    }

    @Override
    public void sendPacketNear(Vec3 pos, double radius, ServerLevel dimension, NetworkMessage packet) {
        sendPacketToPlayers(PlayerLookup.around(dimension, pos, radius), packet);
    }

    @Override
    public void sendPacketTracking(Entity entity, NetworkMessage packet) {
        sendPacketToPlayers(PlayerLookup.tracking(entity), packet);
    }

    private void sendPacketToPlayers(Collection<ServerPlayer> players, NetworkMessage packet) {
        var pkt = ServerPlayNetworking.createS2CPacket(packet.getFabricId(), packet.toBuf());
        for (var p : players) {
            p.connection.send(pkt);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> toVanillaClientboundPacket(NetworkMessage message) {
        return ServerPlayNetworking.createS2CPacket(message.getFabricId(), message.toBuf());
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> func,
        Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(func::apply, blocks).build();
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
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
    @SuppressWarnings("UnstableApiUsage")
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

    @Override
    public Item.Properties addEquipSlotFabric(EquipmentSlot slot) {
        return new FabricItemSettings().equipmentSlot(s -> slot);
    }

    private static final IXplatTags TAGS = new IXplatTags() {
        @Override
        public TagKey<Item> bottleable() {
            return LiquorTags.Items.create(new ResourceLocation("c", "drink_containing/bottle"));
        }

        @Override
        public TagKey<Item> drinks() {
            return LiquorTags.Items.create(new ResourceLocation("c", "drinks"));
        }

        @Override
        public TagKey<Item> cactusJuiceLike() {
            return LiquorTags.Items.create(new ResourceLocation("c", "drinks/cactus_juice"));
        }

        @Override
        public TagKey<Item> honeyLike() {
            return LiquorTags.Items.create(new ResourceLocation("c", "drinks/honey"));
        }

        @Override
        public TagKey<Item> potionLike() {
            return LiquorTags.Items.create(new ResourceLocation("c", "drinks/magic"));
        }

        @Override
        public TagKey<Item> milkLike() {
            return LiquorTags.Items.create(new ResourceLocation("c", "drinks/milk"));
        }

        @Override
        public TagKey<Item> ominousLike() {
            return LiquorTags.Items.create(new ResourceLocation("c", "drinks/ominous"));
        }

        @Override
        public TagKey<Item> sodaLike() {
            return LiquorTags.Items.create(new ResourceLocation("c", "drinks/soda"));
        }

        @Override
        public TagKey<Item> teaLike() {
            return LiquorTags.Items.create(new ResourceLocation("c", "drinks/tea"));
        }

        @Override
        public TagKey<Item> water() {
            return LiquorTags.Items.create(new ResourceLocation("c", "drinks/water"));
        }

        @Override
        public TagKey<Item> waterLike() {
            return LiquorTags.Items.create(new ResourceLocation("c", "drinks/watery"));
        }
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
                LiquorTags.Items.create(new ResourceLocation("c", "shears"))))
        );
    }

    @Override
    public String getModName(String namespace) {
        if (namespace.equals("c")) {
            return "Common";
        }
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(namespace);
        if (container.isPresent()) {
            return container.get().getMetadata().getName();
        }
        return namespace;
    }

//    private static final Supplier<Registry<>>  = Suppliers.memoize(() ->
//        FabricRegistryBuilder.from(new DefaultedMappedRegistry<>(
//                LiquorAPI.MOD_ID + ":nothing", LiquorRegistries.,
//                Lifecycle.stable(), false))
//            .buildAndRegister()
//    );
}