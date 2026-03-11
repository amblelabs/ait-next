package dev.amble.ait.client;

import dev.amble.ait.client.screen.SonicWheelScreen;
import dev.amble.ait.common.items.ItemSonic;
import dev.amble.ait.common.network.SonicTogglePayload;
import dev.amble.ait.xplat.IClientXplatAbstractions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SonicInputHandler {

    public static void clientTick(Minecraft client) {
        if (client.player == null)
            return;

        if (client.screen != null && !(client.screen instanceof SonicWheelScreen))
            return;

        Player player = client.player;
        ItemStack mainHand = player.getMainHandItem();

        if (!(mainHand.getItem() instanceof ItemSonic))
            return;

        while (AitKeybinds.SONIC_WHEEL.consumeClick()) {
            if (client.screen instanceof SonicWheelScreen) {
                client.setScreen(null);
            } else {
                client.setScreen(SonicWheelScreen.tryCreate(mainHand));
            }
        }

        while (AitKeybinds.SONIC_TOGGLE.consumeClick()) {
            boolean nowOpened = ItemSonic.toggleOpened(mainHand);
            IClientXplatAbstractions.INSTANCE.sendPacketToServer(new SonicTogglePayload(nowOpened));
        }
    }
}
