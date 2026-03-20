package dev.amble.ait.mixin.tardis.manager.client;

import dev.amble.ait.api.tardis.Tardis;
import dev.amble.ait.api.tardis.TardisManager;
import dev.amble.ait.client.impl.tardis.ClientTardisManager;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin implements TardisManager.ManagerLevel<Tardis> {

    @Unique
    private @Nullable ClientTardisManager ait$manager;

    @Override
    public TardisManager<Tardis> ait$initTardisManager() {
        return ait$manager = new ClientTardisManager();
    }

    @Override
    public @Nullable TardisManager<Tardis> ait$getTardisManager() {
        return null;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (this.ait$manager != null)
            this.ait$manager.tick();
    }
}
