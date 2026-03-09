package dev.amble.ait.forge;

import dev.amble.ait.api.AitAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AitAPI.MOD_ID)
public final class ForgeAit {

    public ForgeAit(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
    }
}
