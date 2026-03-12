package dev.amble.ait.common.blocks;

import dev.amble.ait.common.lib.AitBlockEntities;
import dev.amble.ait.common.lib.AitSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PoliceBoxBlockEntity extends BlockEntity implements GeoBlockEntity {

    public enum DoorState {
        CLOSED,
        RIGHT_OPEN,
        BOTH_OPEN
    }

    private static final String ALPHA_KEY = "Alpha";
    private static final String DOOR_STATE_KEY = "DoorState";

    private static final RawAnimation RIGHT_OPEN = RawAnimation.begin().thenPlay("right door open");
    private static final RawAnimation LEFT_OPEN = RawAnimation.begin().thenPlay("left door open");
    private static final RawAnimation RIGHT_CLOSE = RawAnimation.begin().thenPlay("right door close");
    private static final RawAnimation LEFT_CLOSE = RawAnimation.begin().thenPlay("left door close");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private float alpha = 1.0f;
    private DoorState doorState = DoorState.CLOSED;
    private boolean rightHasOpened = false;
    private boolean leftHasOpened = false;
    private boolean needsSnap = false;

    public PoliceBoxBlockEntity(BlockPos pos, BlockState state) {
        super(AitBlockEntities.POLICE_BOX_BLOCK_ENTITY, pos, state);
    }

    public int getTextureIndex() {
        return this.getBlockState().getValue(PoliceBoxBlock.TEXTURE);
    }

    public DoorState getDoorState() {
        return doorState;
    }

    public float getAlpha() {
        return alpha;
    }

    public boolean isOnSlab() {
        return this.getBlockState().getValue(PoliceBoxBlock.ON_SLAB);
    }

    public boolean needsSnap() {
        return needsSnap;
    }

    public void clearSnap() {
        this.needsSnap = false;
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.clamp(alpha, 0.0f, 1.0f);
        this.setChanged();
        if (this.level != null) {
            int light = Math.round(this.alpha * 7.0f);
            BlockState current = this.getBlockState();
            if (current.getValue(PoliceBoxBlock.LIGHT) != light) {
                this.level.setBlock(this.worldPosition, current.setValue(PoliceBoxBlock.LIGHT, light), 3);
            }
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
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

        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat(ALPHA_KEY, this.alpha);
        tag.putInt(DOOR_STATE_KEY, this.doorState.ordinal());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.alpha = tag.contains(ALPHA_KEY) ? tag.getFloat(ALPHA_KEY) : 1.0f;
        int doorOrdinal = tag.getInt(DOOR_STATE_KEY);
        this.doorState = DoorState.values()[Math.clamp(doorOrdinal, 0, DoorState.values().length - 1)];

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
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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


