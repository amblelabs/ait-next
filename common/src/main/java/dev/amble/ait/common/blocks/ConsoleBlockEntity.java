package dev.amble.ait.common.blocks;

import dev.amble.ait.common.lib.AitVariants;
import dev.amble.ait.common.lib.AitBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ConsoleBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final String MODEL_KEY = "ModelVariant";
    private static final String TEXTURE_KEY = "TextureVariant";

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int modelVariant = 0;
    private int textureVariant = 0;

    public ConsoleBlockEntity(BlockPos pos, BlockState state) {
        super(AitBlockEntities.CONSOLE_BLOCK_ENTITY, pos, state);
    }

    public String getModelName() {
        int idx = AitVariants.wrap(this.modelVariant, AitVariants.CONSOLE_VARIANTS.length);
        return AitVariants.CONSOLE_VARIANTS[idx].modelName();
    }

    public String getAnimationName() {
        int idx = AitVariants.wrap(this.modelVariant, AitVariants.CONSOLE_VARIANTS.length);
        return AitVariants.CONSOLE_VARIANTS[idx].animationName();
    }

    public String getTextureName() {
        AitVariants.ConsoleVariant variant = AitVariants.CONSOLE_VARIANTS[
                AitVariants.wrap(this.modelVariant, AitVariants.CONSOLE_VARIANTS.length)
                ];
        String[] textures = variant.textureNames();
        return textures[AitVariants.wrap(this.textureVariant, textures.length)];
    }

    public boolean isOnSlab() {
        return this.getBlockState().getValue(ConsoleBlock.ON_SLAB);
    }

    public boolean isBetween() {
        return this.getBlockState().getValue(ConsoleBlock.BETWEEN);
    }

    public void cycleAnimation() {
        // Consoles now always run idle; keep method for call-site compatibility.
    }

    public void cycleModelVariant() {
        this.modelVariant = (this.modelVariant + 1) % AitVariants.CONSOLE_VARIANTS.length;
        this.textureVariant = 0;
        this.sync();
    }

    public void cycleTextureVariant() {
        AitVariants.ConsoleVariant variant = AitVariants.CONSOLE_VARIANTS[
                AitVariants.wrap(this.modelVariant, AitVariants.CONSOLE_VARIANTS.length)
                ];
        this.textureVariant = (this.textureVariant + 1) % Math.max(variant.textureNames().length, 1);
        this.sync();
    }

    private void sync() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void cycleLight() {
        if (this.level == null) return;

        BlockState current = this.getBlockState();
        int light = (current.getValue(ConsoleBlock.LIGHT) + 1) % 16;
        BlockState updated = current.setValue(ConsoleBlock.LIGHT, light);

        this.setChanged();
        this.level.setBlock(this.worldPosition, updated, Block.UPDATE_ALL);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(MODEL_KEY, this.modelVariant);
        tag.putInt(TEXTURE_KEY, this.textureVariant);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.modelVariant = tag.contains(MODEL_KEY)
                ? AitVariants.wrap(tag.getInt(MODEL_KEY), AitVariants.CONSOLE_VARIANTS.length)
                : 0;

        AitVariants.ConsoleVariant variant = AitVariants.CONSOLE_VARIANTS[this.modelVariant];
        int textureCount = Math.max(variant.textureNames().length, 1);
        this.textureVariant = tag.contains(TEXTURE_KEY)
                ? AitVariants.wrap(tag.getInt(TEXTURE_KEY), textureCount)
                : 0;
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
        controllers.add(new AnimationController<>(this, "console", 5, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}


