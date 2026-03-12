package dev.amble.ait.client.screen;

import dev.amble.ait.client.AitKeybinds;
import dev.amble.ait.client.screen.wheel.*;
import dev.amble.ait.client.screen.wheel.action.Action;
import dev.amble.ait.common.items.ItemCrystal;
import dev.amble.ait.common.items.ItemSonic;
import dev.amble.ait.common.items.components.SonicCrystals;
import dev.amble.ait.common.lib.AitComponents;
import dev.amble.ait.common.sonic.SonicCrystal;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * @author drtheodor
 * From hex-spell-wheel
 */
public class SonicWheelScreen extends AbstractWheelScreen {

    // Outer ring radii
    public static final float OUTER_INNER_RADIUS = 55f;
    public static final float OUTER_OUTER_RADIUS = 90f;

    // Inner ring radii
    public static final float INNER_INNER_RADIUS = 15f;
    public static final float INNER_OUTER_RADIUS = 50f;

    private final WidgetSet outer;
    private final WidgetSet inner;

    public SonicWheelScreen(WidgetSet outer, WidgetSet inner) {
        this.outer = outer;
        this.inner = inner;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int cy = height / 2;

        outer.forEach((slot, widget) -> {
            if (widget != null) addRenderableWidget(new WheelOptionWidget(
                cx, cy, OUTER_INNER_RADIUS, OUTER_OUTER_RADIUS, slot, widget, true));
        });

        inner.forEach((slot, widget) -> {
            if (widget != null) addRenderableWidget(new WheelOptionWidget(
                cx, cy, INNER_INNER_RADIUS, INNER_OUTER_RADIUS, slot, widget, true));
        });
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (AitKeybinds.SONIC_WHEEL.matches(i, j)) {
            this.onClose();
            return true;
        }

        if (super.keyPressed(i, j, k))
            return true;

        KeyMapping[] hotbar = Objects.requireNonNull(minecraft).options.keyHotbarSlots;

        //noinspection ForLoopReplaceableByForEach - TODO: use index for quick keybinds
        for (int l = 0; l < hotbar.length; l++) {
            if (hotbar[l].matches(i, j)) {
//                this.simulateClick(l);
                return true;
            }
        }

        return false;
    }

    public static @Nullable AbstractWheelScreen tryCreate(ItemStack stack) {
        return tryCreate(stack, WidgetSet.create(new Widget[0]));
    }

    private static @Nullable AbstractWheelScreen tryCreate(ItemStack stack, WidgetSet outer) {
        SonicCrystals crystals = stack.get(AitComponents.SONIC_CRYSTALS);
        if (crystals == null) return null;

        @Nullable Widget[] inner = new @Nullable Widget[] {
                null, // TOP_LEFT
                ListFunctionsAction.widget(stack, crystals, 0), // TOP
                null, // TOP_RIGHT

                ListFunctionsAction.widget(stack, crystals, 1), // LEFT
                ListFunctionsAction.widget(stack, crystals, 2), // RIGHT

                null, // BOTTOM_LEFT
                ListFunctionsAction.widget(stack, crystals, 3), // BOTTOM
                null, // BOTTOM_RIGHT
        };

        return new SonicWheelScreen(outer, WidgetSet.create(inner));
    }

    record ListFunctionsAction(ItemStack sonic, SonicCrystal crystal, int crystalIdx) implements Action {

        static Widget widget(ItemStack sonic, SonicCrystals crystals, int crystalIdx) {
            ItemStack crystalStack = crystals.getItem(crystalIdx);
            if (crystalStack == null) return Widget.empty();

            SonicCrystal crystal = ((ItemCrystal) crystalStack.getItem()).getCrystal();
            return Widget.fromStack(crystalStack, new ListFunctionsAction(sonic, crystal, crystalIdx), true);
        }

        @Override
        public void run(Minecraft client, Widget widget) {
            Widget[] outer = new Widget[8];

            int i = 0;
            for (SonicCrystal.SonicFunction function : crystal.functions()) {
                outer[i] = new Widget(function.name(), function.preview(), new SonicFunctionAction(sonic, crystalIdx * 8 + i), false);
                i++;
            }

            client.setScreen(SonicWheelScreen.tryCreate(sonic, WidgetSet.create(outer, Widget.empty())));
        }
    }

    record SonicFunctionAction(ItemStack sonic, int funcIdx) implements Action {

        @Override
        public void run(Minecraft client, Widget widget) {
            ItemSonic.setFunction(sonic, funcIdx);
        }
    }
}