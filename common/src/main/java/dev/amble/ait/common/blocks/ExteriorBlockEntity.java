package dev.amble.ait.common.blocks;

import dev.amble.ait.api.mod.block.entity.LinkableBlockEntity;
import dev.amble.ait.api.tardis.Tardis;
import dev.amble.ait.common.impl.tardis.state.DoorState;
import dev.amble.ait.common.lib.AitBlockEntities;
import dev.amble.ait.common.lib.AitVariants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ExteriorBlockEntity extends LinkableBlockEntity implements GeoBlockEntity {

    private static final String ALPHA_KEY = "Alpha";
    private static final String MODEL_KEY = "ModelVariant";
    private static final String TEXTURE_KEY = "TextureVariant";

    private static final RawAnimation RIGHT_OPEN = RawAnimation.begin().thenPlay("right door open");
    private static final RawAnimation LEFT_OPEN = RawAnimation.begin().thenPlay("left door open");
    private static final RawAnimation RIGHT_CLOSE = RawAnimation.begin().thenPlay("right door close");
    private static final RawAnimation LEFT_CLOSE = RawAnimation.begin().thenPlay("left door close");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private float alpha = 1.0f;
    private int modelVariant = 0;
    private int textureVariant = 0;
    private boolean rightHasOpened = false;
    private boolean leftHasOpened = false;
    private boolean needsSnap = false;

    public ExteriorBlockEntity(BlockPos pos, BlockState state) {
        super(AitBlockEntities.EXTERIOR_BLOCK_ENTITY, pos, state);
        this.textureVariant = state.hasProperty(ExteriorBlock.TEXTURE) ? state.getValue(ExteriorBlock.TEXTURE) : 0;
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

        if (this.level != null && this.getBlockState().hasProperty(ExteriorBlock.TEXTURE)) {
            BlockState state = this.getBlockState().setValue(ExteriorBlock.TEXTURE, this.textureVariant);
            this.level.setBlock(this.worldPosition, state, 3);
        }

        this.sync();
    }

    public float getAlpha() {
        return alpha;
    }

    public boolean isOnSlab() {
        return this.getBlockState().getValue(ExteriorBlock.ON_SLAB);
    }

    public boolean needsSnap() {
        return needsSnap;
    }

    public void clearSnap() {
        this.needsSnap = false;
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
        tag.putFloat(ALPHA_KEY, this.alpha);
        tag.putInt(MODEL_KEY, this.modelVariant);
        tag.putInt(TEXTURE_KEY, this.textureVariant);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.alpha = tag.contains(ALPHA_KEY) ? tag.getFloat(ALPHA_KEY) : 1.0f;

        this.modelVariant = tag.contains(MODEL_KEY)
                ? AitVariants.wrap(tag.getInt(MODEL_KEY), AitVariants.EXTERIOR_MODEL_NAMES.length)
                : 0;
        this.textureVariant = tag.contains(TEXTURE_KEY)
                ? AitVariants.wrap(tag.getInt(TEXTURE_KEY), AitVariants.EXTERIOR_TEXTURE_NAMES.length)
                : (this.getBlockState().hasProperty(ExteriorBlock.TEXTURE)
                ? this.getBlockState().getValue(ExteriorBlock.TEXTURE)
                : 0);
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

            Tardis tardis = this.tardis();
            if (tardis == null) return PlayState.STOP;

            DoorState door = tardis.resolveState(DoorState.state);

            if (door.rightOpen) {
                rightHasOpened = true;
                return state.setAndContinue(RIGHT_OPEN);
            }

            if (!rightHasOpened) return PlayState.STOP;
            return state.setAndContinue(RIGHT_CLOSE);
        }));

        controllers.add(new AnimationController<>(this, "left_door", 0, state -> {
            if (needsSnap) return PlayState.STOP;

            Tardis tardis = this.tardis();
            if (tardis == null) return PlayState.STOP;

            DoorState door = tardis.resolveState(DoorState.state);

            if (door.leftOpen) {
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


