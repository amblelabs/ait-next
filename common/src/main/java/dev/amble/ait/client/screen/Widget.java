package dev.amble.ait.client.screen;

import dev.amble.ait.common.I18n;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author drtheodor
 * From hex-spell-wheel
 */
public final class Widget {

    private static final ItemStack EMPTY_STACK = new ItemStack(Items.BARRIER);

    public static final int NORMAL_COLOR = 0x2c2c2c;
    public static final int HOVER_COLOR = 0x3c8527;

    private final Component label;
    private ItemStack preview;

    private final Action actions;
    private final boolean keepOpened;

    private int normalColor = NORMAL_COLOR;
    private int hoverColor = HOVER_COLOR;

    public static Widget fromStack(ItemStack stack, Action action, boolean keepOpened) {
        return stack == null ? null : new Widget(stack.getDisplayName(), stack, action, keepOpened);
    }

    public Widget(Component label, ItemStack preview, Action actions, boolean keepOpened) {
        this.label = label;
        this.preview = preview;
        this.actions = actions;
        this.keepOpened = keepOpened;
    }

    public Widget(Component label, Item item, Action... actions) {
        this(label, new ItemStack(item), Action.and(actions), false);
    }

    public void run(Minecraft client) {
        if (actions == null)
            return;

        actions.run(client, this);
    }

    public void runAlt(Minecraft client) {
        if (actions == null)
            return;

        actions.runAlt(client, this);
    }

    public static Widget empty() {
        return new Widget(I18n.EMPTY_WIDGET, EMPTY_STACK, null, true);
    }

    public Component label() {
        return label;
    }

    public ItemStack preview() {
        return preview;
    }

    public void setPreview(ItemStack preview) {
        this.preview = preview;
    }

    @Nullable
    public Action actions() {
        return actions;
    }

    public boolean keepOpened() {
        return keepOpened;
    }

    public int currentColor() {
        return normalColor;
    }

    public int hoverColor() {
        return hoverColor;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public void setHoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Widget) obj;
        return Objects.equals(this.label, that.label) &&
                Objects.equals(this.preview, that.preview) &&
                Objects.equals(this.actions, that.actions) &&
                this.keepOpened == that.keepOpened;
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, preview, actions, keepOpened);
    }
}