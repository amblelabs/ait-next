package dev.amble.ait.common.blocks;

import dev.amble.ait.api.tardis.Tardis;
import dev.amble.ait.common.impl.tardis.state.ExteriorState;
import dev.amble.ait.common.lib.AitEntities;
import dev.amble.ait.common.lib.AitSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

// FIXME: this looks like a remnant of ait 1
public class FallingTardisBlockEntity extends Entity implements GeoEntity {

    private static final EntityDataAccessor<CompoundTag> DATA_BLOCK_STATE_TAG =
            SynchedEntityData.defineId(FallingTardisBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<CompoundTag> DATA_BLOCK_ENTITY_TAG =
            SynchedEntityData.defineId(FallingTardisBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private BlockState blockState = Blocks.AIR.defaultBlockState();
    private @Nullable CompoundTag blockEntityData;
    private int time;

    public FallingTardisBlockEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public FallingTardisBlockEntity(Level level, double x, double y, double z, BlockState state, CompoundTag blockEntityData) {
        this(AitEntities.FALLING_TARDIS_BLOCK, level);
        this.blockState = state;
        this.blockEntityData = blockEntityData;
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        syncToClient();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static @Nullable FallingTardisBlockEntity fall(Level level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return null;

        CompoundTag beData = be.saveWithoutMetadata(level.registryAccess());

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        FallingTardisBlockEntity entity = new FallingTardisBlockEntity(
                level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, state, beData);
        level.addFreshEntity(entity);

        return entity;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BLOCK_STATE_TAG, new CompoundTag());
        builder.define(DATA_BLOCK_ENTITY_TAG, new CompoundTag());
    }

    private void syncToClient() {
        this.entityData.set(DATA_BLOCK_STATE_TAG, NbtUtils.writeBlockState(this.blockState));
        this.entityData.set(DATA_BLOCK_ENTITY_TAG, this.blockEntityData != null ? this.blockEntityData : new CompoundTag());
    }

    public BlockState getClientBlockState() {
        if (!this.level().isClientSide()) return this.blockState;
        CompoundTag tag = this.entityData.get(DATA_BLOCK_STATE_TAG);
        if (tag.isEmpty()) return this.blockState;
        return NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag);
    }

    public CompoundTag getClientBlockEntityData() {
        return this.entityData.get(DATA_BLOCK_ENTITY_TAG);
    }

    public int getTextureIndex() {
        BlockState state = this.level().isClientSide() ? getClientBlockState() : this.blockState;
        if (state.hasProperty(ExteriorBlock.TEXTURE)) {
            return state.getValue(ExteriorBlock.TEXTURE);
        }
        return 0;
    }

    public int getRotation() {
        BlockState state = this.level().isClientSide() ? getClientBlockState() : this.blockState;
        if (state.hasProperty(ExteriorBlock.ROTATION)) {
            return state.getValue(ExteriorBlock.ROTATION);
        }
        return 0;
    }

    public float getAlpha() {
        CompoundTag data = this.level().isClientSide() ? getClientBlockEntityData() : this.blockEntityData;
        if (data.contains("Alpha")) {
            return data.getFloat("Alpha");
        }
        return 1.0f;
    }

    public int getDoorStateOrdinal() {
        CompoundTag data = this.level().isClientSide() ? getClientBlockEntityData() : this.blockEntityData;
        if (data.contains("DoorState")) {
            return data.getInt("DoorState");
        }
        return 0;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide() && this.blockState.isAir()) {
            this.discard();
            return;
        }

        this.time++;

        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (!this.level().isClientSide()) {
            BlockPos landingPos = this.blockPosition();

            if (this.onGround()) {
                BlockState stateAtPos = this.level().getBlockState(landingPos);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));

                if (!stateAtPos.is(Blocks.MOVING_PISTON)) {
                    if (stateAtPos.canBeReplaced()) {
                        BlockState toPlace = setOnSlab(this.blockState, false);

                        if (this.level().setBlock(landingPos, toPlace, Block.UPDATE_ALL)) {
                            this.level().playSound(null, landingPos, AitSounds.LAND_THUD, SoundSource.BLOCKS, 1.0f, 1.0f);
                            this.restorePlacedBlockData(landingPos, toPlace);
                            this.discard();
                            return;
                        }
                    } else if (isBottomSlab(stateAtPos)) {
                        BlockPos abovePos = landingPos.above();
                        BlockState aboveState = this.level().getBlockState(abovePos);

                        if (aboveState.canBeReplaced()) {
                            BlockState toPlace = setOnSlab(this.blockState, true);

                            if (this.level().setBlock(abovePos, toPlace, Block.UPDATE_ALL)) {
                                this.level().playSound(null, abovePos, AitSounds.LAND_THUD, SoundSource.BLOCKS, 1.0f, 1.0f);
                                this.restorePlacedBlockData(abovePos, toPlace);
                                this.discard();
                                return;
                            }
                        }
                    }
                }
            }

            if (this.time > 600) {
                this.discard();
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
    }

    private void restorePlacedBlockData(BlockPos placedPos, BlockState placedState) {
        BlockEntity be = this.level().getBlockEntity(placedPos);
        if (be != null && this.blockEntityData != null) {
            be.loadWithComponents(this.blockEntityData, this.level().registryAccess());
            be.setChanged();
        }

        this.level().sendBlockUpdated(placedPos, placedState, placedState, Block.UPDATE_ALL);

        if (be instanceof ExteriorBlockEntity exteriorBlockEntity) {
            Tardis tardis = exteriorBlockEntity.tardis();
            if (tardis != null) {
                ExteriorState exteriorState = tardis.state(ExteriorState.state);
                exteriorState.updateExteriorPos(new GlobalPos(this.level().dimension(), placedPos), (byte) this.getRotation());
            }
        }
    }

    private static boolean isBottomSlab(BlockState state) {
        return state.getBlock() instanceof SlabBlock
                && state.hasProperty(BlockStateProperties.SLAB_TYPE)
                && state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM;
    }

    private static BlockState setOnSlab(BlockState state, boolean onSlab) {
        Property<?> prop = state.getBlock().getStateDefinition().getProperty("on_slab");
        if (prop instanceof BooleanProperty bp) {
            return state.setValue(bp, onSlab);
        }
        return state;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("BlockState", NbtUtils.writeBlockState(this.blockState));
        tag.putInt("Time", this.time);
        if (this.blockEntityData != null) {
            tag.put("TileEntityData", this.blockEntityData);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.blockState = NbtUtils.readBlockState(
                this.level().holderLookup(Registries.BLOCK), tag.getCompound("BlockState"));
        this.time = tag.getInt("Time");
        if (tag.contains("TileEntityData", 10)) {
            this.blockEntityData = tag.getCompound("TileEntityData");
        }
        if (this.blockState.isAir()) {
            this.blockState = Blocks.AIR.defaultBlockState();
        }
        syncToClient();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 0, state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
