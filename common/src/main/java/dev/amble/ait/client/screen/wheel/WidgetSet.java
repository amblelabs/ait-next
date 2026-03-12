package dev.amble.ait.client.screen.wheel;

import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * @author drtheodor
 * From hex-spell-wheel
 */
public class WidgetSet {
    public static final int SET_SIZE = 8;
    protected final @Nullable Widget[] widgets = new Widget[SET_SIZE];

    private WidgetSet() { }

    @SuppressWarnings("unused")
    public @Nullable Widget get(int index) {
        if (index < 0 || index > widgets.length - 1)
            return null;

        return widgets[index];
    }

    public static WidgetSet create(@Nullable Widget[] list) {
        return create(list, null);
    }

    public static WidgetSet create(@Nullable Widget[] list, @Nullable Widget filler) {
        WidgetSet set = new WidgetSet();

        if (filler != null)
            Arrays.fill(set.widgets, filler);

        if (list.length == 0)
            return set;

        int s = Math.min(list.length, SET_SIZE);

        for (int i = 0; i < s; i++) {
            Widget widget = list[i];

            if (widget != null)
                set.widgets[i] = widget;
        }

        return set;
    }

    public void forEach(BiConsumer<WidgetSlot, @Nullable Widget> action) {
        for (int i = 0; i < SET_SIZE; i++) {
            action.accept(WidgetSlot.VALUES[i], widgets[i]);
        }
    }
}