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

public class PoliceBoxBlockEntity extends BlockEntity {

    private static final String TEXTURE_INDEX_KEY = "TextureIndex";
    private static final String ALPHA_KEY = "Alpha";
    public static final int TEXTURE_COUNT = 5;

    private int textureIndex = 0;
    private float alpha = 1.0f;

    public PoliceBoxBlockEntity(BlockPos pos, BlockState state) {
        super(AitBlockEntities.POLICE_BOX_BLOCK_ENTITY, pos, state);
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public void cycleTexture() {
        this.textureIndex = (this.textureIndex + 1) % TEXTURE_COUNT;
        this.setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.clamp(alpha, 0.0f, 1.0f);
        this.setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TEXTURE_INDEX_KEY, this.textureIndex);
        tag.putFloat(ALPHA_KEY, this.alpha);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.textureIndex = tag.getInt(TEXTURE_INDEX_KEY);
        this.alpha = tag.contains(ALPHA_KEY) ? tag.getFloat(ALPHA_KEY) : 1.0f;
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
}



