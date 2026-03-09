package dev.amble.ait.client.screen.wheel;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * @author drtheodor
 * From hex-spell-wheel
 */
public enum WidgetSlot implements StringRepresentable {

    TOP_LEFT(-21-32, -21-32, -8, -8),
    TOP(-32/2, -21-32-5, 0, -8),
    TOP_RIGHT(21, -21-32, 8, -8),

    LEFT(-21-32-5, -32/2, -8, 0),
    RIGHT(21+5, -32/2, 8, 0),

    BOTTOM_LEFT(-21-32, 21, -8, 8),
    BOTTOM(-32/2, 21+5, 0, 8),
    BOTTOM_RIGHT(21, 21, 8, 8);

    public static final WidgetSlot[] VALUES = WidgetSlot.values();

    final int x;
    final int y;
    final int xOffset;
    final int yOffset;

    WidgetSlot(int x, int y, int xOffset, int yOffset) {
        this.x = x;
        this.y = y;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public int getX(int width) {
        return width / 2 + x;
    }

    public int getY(int height) {
        return height / 2 + y;
    }

    public int getHalfX(int width) {
        return (width + x) / 2;
    }

    public int getHalfY(int height) {
        return (height + y) / 2;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.toString().toLowerCase();
    }
}