package dev.amble.ait.common.network.tardis.manager;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.client.impl.tardis.ClientTardisManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TardisSyncPayload(CompoundTag tag) implements CustomPacketPayload {

    public static final ResourceLocation ID = AitAPI.modLoc("tardis/sync");
    public static final CustomPacketPayload.Type<TardisSyncPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, TardisSyncPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.TRUSTED_COMPOUND_TAG, TardisSyncPayload::tag, TardisSyncPayload::new);

    @Environment(EnvType.CLIENT)
    public void handle(Minecraft minecraft, LocalPlayer player) {
        ClientTardisManager.get(minecraft.level).upsert(tag);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
