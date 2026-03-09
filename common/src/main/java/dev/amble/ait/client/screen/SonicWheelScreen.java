package dev.amble.ait.client.screen;

import dev.amble.ait.client.screen.wheel.*;
import dev.amble.ait.client.screen.wheel.action.Action;
import dev.amble.ait.common.items.components.SonicCrystals;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * @author drtheodor
 * From hex-spell-wheel
 */
public class SonicWheelScreen extends AbstractWheelScreen {

    public static final float OUTER_RING_SPREAD_DEFAULT = 1.87f;
    public static final int OUTER_RING_SIZE_DEFAULT = 32;

    public static final float INNER_RING_SPREAD_DEFAULT = -1.63f;
    public static final int INNER_RING_SIZE_DEFAULT = 24;

    private final WidgetSet outer;
    private final WidgetSet inner;

    public SonicWheelScreen(WidgetSet outer, WidgetSet inner) {
        this.outer = outer;
        this.inner = inner;
    }

    @Override
    protected void init() {
        outer.forEach((slot, widget) -> {
            if (widget != null) addRenderableWidget(new WheelOptionWidget(
                slot.getX(width), slot.getY(height), widget, slot.getXOffset(), slot.getYOffset(), OUTER_RING_SIZE_DEFAULT, OUTER_RING_SPREAD_DEFAULT, true));
        });

        inner.forEach((slot, widget) -> {
            if (widget != null) addRenderableWidget(new WheelOptionWidget(
                slot.getX(width), slot.getY(height), widget, slot.getXOffset(), slot.getYOffset(), INNER_RING_SIZE_DEFAULT, INNER_RING_SPREAD_DEFAULT, true));
        });
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k))
            return true;

        KeyMapping[] hotbar = minecraft.options.keyHotbarSlots;

        for (int l = 0; l < hotbar.length; l++) {
            if (hotbar[l].matches(i, j)) {
//                this.simulateClick(l);
                return true;
            }
        }

        return false;
    }

    public static AbstractWheelScreen tryCreate(SonicCrystals crystals) {
        Action doShitNothing = new Action() {
            @Override
            public void run(Minecraft client, Widget widget) {
                client.gui.setOverlayMessage(Component.literal("clicked on crystal"), false);
            }
        };

        Widget[] inner = new Widget[] {
                null, // TOP_LEFT
                Widget.fromStack(crystals.getItem(0), doShitNothing, true), // TOP
                null, // TOP_RIGHT

                Widget.fromStack(crystals.getItem(1), doShitNothing, true), // LEFT
                Widget.fromStack(crystals.getItem(2), doShitNothing, true), // RIGHT

                null, // BOTTOM_LEFT
                Widget.fromStack(crystals.getItem(3), doShitNothing, true), // BOTTOM
                null, // BOTTOM_RIGHT
        };

        return new SonicWheelScreen(WidgetSet.create(new Widget[0]), WidgetSet.create(inner));
    }
}