package dev.amble.ait.fabric.mixin.multidim;

import com.google.common.collect.Maps;
import dev.amble.lib.multidim.api.MultiDimServer;
import dev.amble.lib.multidim.event.ServerCrashEvent;
import net.minecraft.CrashReport;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MultiDimServer {

    @Shadow @Final protected LevelStorageSource.LevelStorageAccess storageSource;
    @Shadow @Final @Mutable private Map<ResourceKey<Level>, ServerLevel> levels;

    @Override
    public void multidim$addWorld(ServerLevel world) {
        LinkedHashMap<ResourceKey<Level>, ServerLevel> newMap = Maps.newLinkedHashMap();
        newMap.putAll(this.levels);
        newMap.put(world.dimension(), world);
        this.levels = newMap;
    }

    @Override
    public boolean multidim$hasWorld(ResourceKey<Level> key) {
        return this.levels.containsKey(key);
    }

    @Override
    public ServerLevel multidim$removeWorld(ResourceKey<Level> key) {
        LinkedHashMap<ResourceKey<Level>, ServerLevel> newMap = Maps.newLinkedHashMap();
        Map<ResourceKey<Level>, ServerLevel> oldMap = this.levels;

        for (Map.Entry<ResourceKey<Level>, ServerLevel> entry : oldMap.entrySet()) {
            if (!entry.getKey().equals(key)) {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }

        this.levels = newMap;
        return oldMap.get(key);
    }

    @Override
    public LevelStorageSource.LevelStorageAccess multidim$getSession() {
        return this.storageSource;
    }

    @Inject(method = "onServerCrash", at = @At("HEAD"))
    private void multidim$onServerCrash(CrashReport report, CallbackInfo ci) {
        ServerCrashEvent.EVENT.invoker().onServerCrash((MinecraftServer) (Object) this, report);
    }
}


