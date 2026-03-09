package dev.amble.ait.forge;

import dev.amble.ait.api.AitAPI;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AitAPI.MOD_ID)
public final class AITMod {

    public AITMod() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(AitAPI.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
    }
}
