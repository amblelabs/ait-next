package dev.amble.ait.mixin.tardis.manager;

import dev.amble.ait.api.mod.tardis.ServerTardis;
import dev.amble.ait.api.mod.tardis.TardisManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements TardisManager.ManagerLevel<ServerTardis> {

    @Shadow public abstract MinecraftServer getServer();

    @Override
    public TardisManager<ServerTardis> ait$initTardisManager() {
        return TardisManager.<ServerTardis>asManagerLevel(this.getServer()).ait$initTardisManager();
    }

    @Override
    public @Nullable TardisManager<ServerTardis> ait$getTardisManager() {
        return TardisManager.<ServerTardis>asManagerLevel(this.getServer()).ait$getTardisManager();
    }
}
