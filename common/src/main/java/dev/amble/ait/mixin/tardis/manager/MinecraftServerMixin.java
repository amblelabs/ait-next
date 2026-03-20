package dev.amble.ait.mixin.tardis.manager;

import dev.amble.ait.api.tardis.ServerTardis;
import dev.amble.ait.api.tardis.TardisManager;
import dev.amble.ait.common.impl.tardis.ServerTardisManager;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements TardisManager.ManagerLevel<ServerTardis> {

    @Shadow
    @Final
    private static Logger LOGGER;
    @Unique
    private @Nullable ServerTardisManager ait$manager;

    @Override
    public TardisManager<ServerTardis> ait$initTardisManager() {
        return this.ait$manager = new ServerTardisManager((MinecraftServer) (Object) this);
    }

    @Override
    public TardisManager<ServerTardis> ait$getTardisManager() {
        return ait$manager != null ? ait$manager : ait$initTardisManager();
    }

    @Inject(method = "tickServer", at = @At("TAIL"))
    public void tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        if (this.ait$manager != null)
            this.ait$manager.tick();
    }

    @Inject(method = "saveAllChunks", at = @At("TAIL"))
    public void save(boolean suppressLog, boolean flush, boolean forced, CallbackInfoReturnable<Boolean> cir) {
        if (this.ait$manager != null) {
            this.ait$manager.save();
            LOGGER.info("ServerTardisManager: All TARDIS' are saved");
        }
    }
}
