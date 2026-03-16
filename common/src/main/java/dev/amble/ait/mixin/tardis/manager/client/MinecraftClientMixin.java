package dev.amble.ait.mixin.tardis.manager.client;

import dev.amble.ait.api.mod.tardis.Tardis;
import dev.amble.ait.api.mod.tardis.TardisManager;
import dev.amble.ait.client.impl.tardis.ClientTardisManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin implements TardisManager.ManagerLevel<Tardis> {

    @Unique
    private @Nullable ClientTardisManager ait$manager;

    @Inject(method = "setLevel", at = @At("HEAD"))
    public void setWorld(ClientLevel level, ReceivingLevelScreen.Reason reason, CallbackInfo ci) {
        this.ait$manager = null;
    }

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
