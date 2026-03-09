package dev.amble.ait.client.screen;

import java.util.function.BiConsumer;

/**
 * @author drtheodor
 * From hex-spell-wheel
 */
public class WidgetSet {
    public static final int SET_SIZE = 8;
    protected final Widget[] widgets = new Widget[SET_SIZE];

    private WidgetSet() { }

    public Widget get(int index) {
        if (index < 0 || index > widgets.length - 1)
            return null;

        return widgets[index];
    }

    public static WidgetSet create(Widget[] list) {
        WidgetSet set = new WidgetSet();

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

    public void forEach(BiConsumer<WidgetSlot, Widget> action) {
        for (int i = 0; i < SET_SIZE; i++) {
            action.accept(WidgetSlot.VALUES[i], widgets[i]);
        }
    }
}