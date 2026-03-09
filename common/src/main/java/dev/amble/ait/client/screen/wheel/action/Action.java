package dev.amble.ait.client.screen.wheel.action;

import dev.amble.ait.client.screen.wheel.Widget;
import net.minecraft.client.Minecraft;

/**
 * @author drtheodor
 * From hex-spell-wheel
 */
public interface Action {
    default void run(Minecraft client, Widget widget) { }
    default void runAlt(Minecraft client, Widget widget) { }

    static Action and(Action... actions) {
        return actions.length == 1 ? actions[0] : new AndAction(actions);
    }
}