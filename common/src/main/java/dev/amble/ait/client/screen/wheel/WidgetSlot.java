package dev.amble.ait.client.screen.wheel;

import net.minecraft.util.StringRepresentable;

/**
 * @author drtheodor
 * From hex-spell-wheel
 */
public enum WidgetSlot implements StringRepresentable {

    // Angles in degrees, 0 = top, clockwise. Each sector is 45° wide, centered on the cardinal/diagonal.
    TOP_LEFT    (292.5f, 337.5f),
    TOP         (337.5f, 382.5f),  // wraps around 0; handled in angle range checks
    TOP_RIGHT   (22.5f,  67.5f),

    LEFT        (247.5f, 292.5f),
    RIGHT       (67.5f,  112.5f),

    BOTTOM_LEFT (202.5f, 247.5f),
    BOTTOM      (157.5f, 202.5f),
    BOTTOM_RIGHT(112.5f, 157.5f);

    public static final WidgetSlot[] VALUES = WidgetSlot.values();

    private final float startAngle;
    private final float endAngle;

    WidgetSlot(float startAngle, float endAngle) {
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }

    /**
     * Start angle in degrees (0 = top, clockwise).
     */
    public float startAngle() {
        return startAngle;
    }

    /**
     * End angle in degrees (0 = top, clockwise).
     */
    public float endAngle() {
        return endAngle;
    }

    @Override
    public String getSerializedName() {
        return this.toString().toLowerCase();
    }
}