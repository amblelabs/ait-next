package dev.amble.ait.common.blocks;

import dev.amble.ait.common.lib.AitBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ConsoleBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final String ANIM_KEY = "Animation";

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("Idle");
    private static final RawAnimation FLIGHT = RawAnimation.begin().thenLoop("Flight");
    private static final RawAnimation[] ANIMATIONS = { IDLE, FLIGHT };

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int animationIndex = 0;

    public ConsoleBlockEntity(BlockPos pos, BlockState state) {
        super(AitBlockEntities.CONSOLE_BLOCK_ENTITY, pos, state);
    }

    public int getAnimationIndex() {
        return animationIndex;
    }

    public boolean isOnSlab() {
        return this.getBlockState().getValue(ConsoleBlock.ON_SLAB);
    }

    public void cycleAnimation() {
        this.animationIndex = (this.animationIndex + 1) % ANIMATIONS.length;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(ANIM_KEY, this.animationIndex);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.animationIndex = tag.contains(ANIM_KEY) ? tag.getInt(ANIM_KEY) % ANIMATIONS.length : 0;
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
        controllers.add(new AnimationController<>(this, "console", 5, state -> state.setAndContinue(ANIMATIONS[this.animationIndex % ANIMATIONS.length])));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}


