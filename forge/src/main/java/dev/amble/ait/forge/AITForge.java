package dev.amble.ait.forge;

import dev.amble.ait.api.AITAPI;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AITAPI.MOD_ID)
public final class AITMod {

    public AITMod() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(AITAPI.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
    }
}
