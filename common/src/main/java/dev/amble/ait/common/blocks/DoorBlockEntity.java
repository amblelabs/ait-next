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

public class DoorBlockEntity extends BlockEntity implements GeoBlockEntity {

    public enum DoorState {
        CLOSED,
        RIGHT_OPEN,
        BOTH_OPEN
    }

    private static final String DOOR_STATE_KEY = "DoorState";

    private static final RawAnimation RIGHT_OPEN = RawAnimation.begin().thenPlay("right door open");
    private static final RawAnimation LEFT_OPEN = RawAnimation.begin().thenPlay("left door open");
    private static final RawAnimation RIGHT_CLOSE = RawAnimation.begin().thenPlay("right door close");
    private static final RawAnimation LEFT_CLOSE = RawAnimation.begin().thenPlay("left door close");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private DoorState doorState = DoorState.CLOSED;

    public DoorBlockEntity(BlockPos pos, BlockState state) {
        super(AitBlockEntities.DOOR_BLOCK_ENTITY, pos, state);
    }

    public int getTextureIndex() {
        return this.getBlockState().getValue(DoorBlock.TEXTURE);
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
        tag.putInt(DOOR_STATE_KEY, this.doorState.ordinal());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int doorOrdinal = tag.getInt(DOOR_STATE_KEY);
        this.doorState = DoorState.values()[Math.clamp(doorOrdinal, 0, DoorState.values().length - 1)];
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
        controllers.add(new AnimationController<>(this, "right_door", 2, state -> {
            if (doorState == DoorState.RIGHT_OPEN || doorState == DoorState.BOTH_OPEN) {
                return state.setAndContinue(RIGHT_OPEN);
            }
            return state.setAndContinue(RIGHT_CLOSE);
        }));
        controllers.add(new AnimationController<>(this, "left_door", 2, state -> {
            if (doorState == DoorState.BOTH_OPEN) {
                return state.setAndContinue(LEFT_OPEN);
            }
            return state.setAndContinue(LEFT_CLOSE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
