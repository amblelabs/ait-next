package dev.amble.ait.common.blocks;

import dev.amble.ait.api.mod.block.entity.LinkableBlockEntity;
import dev.amble.ait.common.lib.AitBlockEntities;
import dev.amble.ait.common.lib.AitVariants;
import dev.amble.ait.common.lib.AitSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DoorBlockEntity extends LinkableBlockEntity implements GeoBlockEntity {

    public enum DoorState {
        CLOSED,
        RIGHT_OPEN,
        BOTH_OPEN
    }

    private static final String DOOR_STATE_KEY = "DoorState";
    private static final String MODEL_KEY = "ModelVariant";
    private static final String TEXTURE_KEY = "TextureVariant";

    private static final RawAnimation RIGHT_OPEN = RawAnimation.begin().thenPlay("right door open");
    private static final RawAnimation LEFT_OPEN = RawAnimation.begin().thenPlay("left door open");
    private static final RawAnimation RIGHT_CLOSE = RawAnimation.begin().thenPlay("right door close");
    private static final RawAnimation LEFT_CLOSE = RawAnimation.begin().thenPlay("left door close");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private DoorState doorState = DoorState.CLOSED;
    private int modelVariant = 0;
    private int textureVariant = 0;
    private boolean rightHasOpened = false;
    private boolean leftHasOpened = false;
    private boolean needsSnap = false;

    public DoorBlockEntity(BlockPos pos, BlockState state) {
        super(AitBlockEntities.DOOR_BLOCK_ENTITY, pos, state);
        this.textureVariant = state.hasProperty(DoorBlock.TEXTURE) ? state.getValue(DoorBlock.TEXTURE) : 0;
    }

    public int getTextureIndex() {
        return this.textureVariant;
    }

    public String getModelName() {
        return AitVariants.EXTERIOR_MODEL_NAMES[
                AitVariants.wrap(this.modelVariant, AitVariants.EXTERIOR_MODEL_NAMES.length)
                ];
    }

    public String getTextureName() {
        return AitVariants.EXTERIOR_TEXTURE_NAMES[
                AitVariants.wrap(this.textureVariant, AitVariants.EXTERIOR_TEXTURE_NAMES.length)
                ];
    }

    public void cycleModelVariant() {
        this.modelVariant = (this.modelVariant + 1) % AitVariants.EXTERIOR_MODEL_NAMES.length;
        this.sync();
    }

    public void cycleTextureVariant() {
        this.textureVariant = (this.textureVariant + 1) % AitVariants.EXTERIOR_TEXTURE_NAMES.length;

        if (this.level != null && this.getBlockState().hasProperty(DoorBlock.TEXTURE)) {
            BlockState state = this.getBlockState().setValue(DoorBlock.TEXTURE, this.textureVariant);
            this.level.setBlock(this.worldPosition, state, 3);
        }

        this.sync();
    }

    public DoorState getDoorState() {
        return doorState;
    }

    public boolean isOnSlab() {
        return this.getBlockState().getValue(DoorBlock.ON_SLAB);
    }

    public boolean isBetween() {
        return this.getBlockState().getValue(DoorBlock.BETWEEN);
    }

    public boolean needsSnap() {
        return needsSnap;
    }

    public void clearSnap() {
        this.needsSnap = false;
    }

    public void interact(boolean crouching) {
        if (this.getLevel() == null) return;
        switch (doorState) {
            case CLOSED -> {
                if (crouching) {
                    doorState = DoorState.BOTH_OPEN;
                } else {
                    doorState = DoorState.RIGHT_OPEN;
                }
                this.getLevel().playSound(null, this.getBlockPos(), AitSounds.DOOR_OPEN, SoundSource.BLOCKS, 0.5f, 1.0f);
            }
            case RIGHT_OPEN -> {
                if (crouching) {
                    doorState = DoorState.CLOSED;
                } else {
                    doorState = DoorState.BOTH_OPEN;
                }
                this.getLevel().playSound(null, this.getBlockPos(), AitSounds.DOOR_OPEN, SoundSource.BLOCKS, 0.5f, 1.0f);
            }
            case BOTH_OPEN -> {
                doorState = DoorState.CLOSED;
                this.getLevel().playSound(null, this.getBlockPos(), AitSounds.DOOR_CLOSE, SoundSource.BLOCKS, 0.6f, 1.0f);
            }
        }

        this.sync();
    }

    private void sync() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(DOOR_STATE_KEY, this.doorState.ordinal());
        tag.putInt(MODEL_KEY, this.modelVariant);
        tag.putInt(TEXTURE_KEY, this.textureVariant);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int doorOrdinal = tag.getInt(DOOR_STATE_KEY);
        this.doorState = DoorState.values()[Math.clamp(doorOrdinal, 0, DoorState.values().length - 1)];
        this.modelVariant = tag.contains(MODEL_KEY)
                ? AitVariants.wrap(tag.getInt(MODEL_KEY), AitVariants.EXTERIOR_MODEL_NAMES.length)
                : 0;
        this.textureVariant = tag.contains(TEXTURE_KEY)
                ? AitVariants.wrap(tag.getInt(TEXTURE_KEY), AitVariants.EXTERIOR_TEXTURE_NAMES.length)
                : (this.getBlockState().hasProperty(DoorBlock.TEXTURE)
                ? this.getBlockState().getValue(DoorBlock.TEXTURE)
                : 0);

        if (doorState != DoorState.CLOSED) {
            needsSnap = true;
            rightHasOpened = true;
            if (doorState == DoorState.BOTH_OPEN) {
                leftHasOpened = true;
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "right_door", 0, state -> {
            if (needsSnap) return PlayState.STOP;
            if (doorState == DoorState.RIGHT_OPEN || doorState == DoorState.BOTH_OPEN) {
                rightHasOpened = true;
                return state.setAndContinue(RIGHT_OPEN);
            }
            if (!rightHasOpened) return PlayState.STOP;
            return state.setAndContinue(RIGHT_CLOSE);
        }));
        controllers.add(new AnimationController<>(this, "left_door", 0, state -> {
            if (needsSnap) return PlayState.STOP;
            if (doorState == DoorState.BOTH_OPEN) {
                leftHasOpened = true;
                return state.setAndContinue(LEFT_OPEN);
            }
            if (!leftHasOpened) return PlayState.STOP;
            return state.setAndContinue(LEFT_CLOSE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
