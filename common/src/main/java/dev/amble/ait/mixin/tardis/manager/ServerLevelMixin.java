package dev.amble.ait.mixin.tardis.manager;

import dev.amble.ait.api.mod.tardis.ServerTardis;
import dev.amble.ait.api.mod.tardis.TardisManager;
import dev.amble.ait.common.impl.tardis.ServerTardisManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements TardisManager.ManagerLevel<ServerTardis> {

    @Unique
    private @Nullable ServerTardisManager ait$manager;

    @Override
    public TardisManager<ServerTardis> ait$initTardisManager() {
        return this.ait$manager = new ServerTardisManager((ServerLevel) (Object) this);
    }

    @Override
    public @Nullable TardisManager<ServerTardis> ait$getTardisManager() {
        return ait$manager;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        if (this.ait$manager != null)
            this.ait$manager.tick();
    }

    @Inject(method = "save", at = @At("HEAD"))
    private void save(ProgressListener progress, boolean flush, boolean skipSave, CallbackInfo ci) {
        if (this.ait$manager != null)
            this.ait$manager.save();
    }
}
