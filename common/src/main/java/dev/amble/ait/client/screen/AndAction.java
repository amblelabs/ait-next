package dev.amble.ait.client.screen;

import net.minecraft.client.Minecraft;

/**
 * @author drtheodor
 * From hex-spell-wheel
 */
public record AndAction(Action... actions) implements Action {

    @Override
    public void run(Minecraft client, Widget widget) {
        for (Action action : actions) {
            action.run(client, widget);
        }
    }

    @Override
    public void runAlt(Minecraft client, Widget widget) {
        for (Action action : actions) {
            action.runAlt(client, widget);
        }
    }
}