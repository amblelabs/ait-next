package dev.amble.ait.fabric.mixin;

import dev.amble.lib.multidim.event.WorldSaveEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class MultiDimServerLevelMixin {

    @Inject(method = "save", at = @At("HEAD"))
    private void multidim$save(ProgressListener progress, boolean flush, boolean skipSave, CallbackInfo ci) {
        WorldSaveEvent.EVENT.invoker().onWorldSave((ServerLevel) (Object) this);
    }
}



