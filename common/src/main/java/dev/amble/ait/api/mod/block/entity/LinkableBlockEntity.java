package dev.amble.ait.api.mod.block.entity;

import dev.amble.ait.api.tardis.ServerTardis;
import dev.amble.ait.api.tardis.Tardis;
import dev.amble.ait.api.tardis.TardisManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class LinkableBlockEntity extends BlockEntity {

    private static final String ID_TAG = "Tardis";

    private @Nullable UUID tardisId;

    /**
     * Lazily initialized, prefer to use the getter ({@link #tardis()}.
     */
    private @Nullable Tardis tardis;

    public LinkableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public @Nullable Tardis tardis() {
        if (this.tardis != null) return this.tardis;

        if (this.tardisId != null)
            return this.tardis = TardisManager.apply(this.getLevel(),
                    manager -> manager.get(this.tardisId));

        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (this.tardisId != null)
            tag.putUUID(ID_TAG, this.tardisId);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (!tag.hasUUID(ID_TAG)) return;
        this.tardisId = tag.getUUID(ID_TAG);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.tryUnmark();
    }

    public void link(Tardis tardis) {
        this.tardisId = tardis.id();
        this.tardis = tardis;

        this.handleLink();
    }

    public void link(UUID id) {
        this.tardisId = id;
        this.tardis = TardisManager.apply(this.getLevel(), manager -> manager.get(id));

        this.handleLink();
    }

    private void tryMark() {
        if (this.getLevel() instanceof ServerLevel serverWorld && this.tardis() != null)
            TardisManager.asChunkTracker(serverWorld).ait$mark(new ChunkPos(this.getBlockPos()), (ServerTardis) this.tardis);
    }

    /**
     * This may seem like it would explode immediately in multiplayer. But it actually doesn't!
     * {@link #tryMark()} gets called in {@link #getUpdateTag(HolderLookup.Provider)}, which would get called when player starts watching a chunk.
     * This means, it'll be marked back up, in case there are other {@link LinkableBlockEntity}s.
     */
    private void tryUnmark() {
        if (this.getLevel() instanceof ServerLevel serverWorld && this.tardis() != null)
            TardisManager.asChunkTracker(serverWorld).ait$unmark(new ChunkPos(this.getBlockPos()), (ServerTardis) this.tardis);
    }

    private void handleLink() {
        this.tryMark();
        this.markUpdated();
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        this.tryMark();

        CompoundTag tag = super.getUpdateTag(registries);

        if (this.tardisId != null)
            tag.putUUID(ID_TAG, this.tardisId);

        return tag;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
}