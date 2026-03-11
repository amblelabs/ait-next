package dev.amble.ait.client.screen.wheel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * @author drtheodor
 * From hex-spell-wheel
 */
public class WheelOptionWidget extends Button {

    private static final float GAP_DEGREES = 2.0f;

    public final Widget widget;
    private final int centerX;
    private final int centerY;
    private final float innerRadius;
    private final float outerRadius;
    private final float startAngle; // degrees, 0=top, clockwise
    private final float endAngle;
    private final boolean hasIcon;

    public WheelOptionWidget(int centerX, int centerY, float innerRadius, float outerRadius,
                             WidgetSlot slot, Widget widget, boolean hasIcon) {
        super(0, 0, 1, 1, Component.empty(), button -> {}, DEFAULT_NARRATION);

        this.centerX = centerX;
        this.centerY = centerY;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.startAngle = slot.startAngle() + GAP_DEGREES;
        this.endAngle = slot.endAngle() - GAP_DEGREES;
        this.widget = widget;
        this.hasIcon = hasIcon;

        this.setTooltip(Tooltip.create(widget.label()));

        // Set a bounding box that circumscribes the arc for accessibility
        int bboxSize = (int) (outerRadius * 2);
        this.setX(centerX - bboxSize / 2);
        this.setY(centerY - bboxSize / 2);
        this.width = bboxSize;
        this.height = bboxSize;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        isHovered = isMouseOver(mouseX, mouseY);
        renderButton(context);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!this.active || !this.visible) return false;

        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < innerRadius || dist > outerRadius) return false;

        // Convert to our angle system (0=top, clockwise)
        double angleRad = Math.atan2(dx, -dy); // gives 0 at top, positive clockwise
        double angleDeg = Math.toDegrees(angleRad);
        if (angleDeg < 0) angleDeg += 360;

        return isAngleInRange(angleDeg, startAngle, endAngle);
    }

    private static boolean isAngleInRange(double angle, float start, float end) {
        // Normalize everything to [0, 360)
        angle = ((angle % 360) + 360) % 360;
        float s = ((start % 360) + 360) % 360;
        float e = ((end % 360) + 360) % 360;

        if (s <= e) {
            return angle >= s && angle <= e;
        } else {
            // Wraps around 0 (e.g., 350 to 10)
            return angle >= s || angle <= e;
        }
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY);
    }

    protected void renderButton(GuiGraphics context) {
        boolean displayHovered = isHovered() && widget.actions() != null;
        int color = (180 << 24) | (displayHovered ? widget.hoverColor() : widget.currentColor());

        drawArcSegment(context, color);

        if (!hasIcon) return;

        // Render item at the midpoint of the arc
        float midAngleDeg = (startAngle + endAngle) / 2f;
        float midRadius = (innerRadius + outerRadius) / 2f;
        double midAngleRad = Math.toRadians(midAngleDeg - 90);
        int iconX = centerX + (int) (midRadius * Math.cos(midAngleRad)) - 8;
        int iconY = centerY + (int) (midRadius * Math.sin(midAngleRad)) - 8;

        ItemStack preview = widget.preview();
        context.renderItem(preview, iconX, iconY);
    }

    /**
     * Draws the arc segment pixel-by-pixel using context.fill() for each scanline row.
     * This goes through MC's standard rendering pipeline and is guaranteed to be visible.
     */
    private void drawArcSegment(GuiGraphics context, int color) {
        int oR = (int) Math.ceil(outerRadius);

        for (int dy = -oR; dy <= oR; dy++) {
            // For this row, find the horizontal extent within the arc
            int startX = Integer.MAX_VALUE;
            int endX = Integer.MIN_VALUE;

            for (int dx = -oR; dx <= oR; dx++) {
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < innerRadius || dist > outerRadius) continue;

                // Angle check (0=top, clockwise)
                double angleRad = Math.atan2(dx, -dy);
                double angleDeg = Math.toDegrees(angleRad);
                if (angleDeg < 0) angleDeg += 360;

                if (isAngleInRange(angleDeg, startAngle, endAngle)) {
                    if (dx < startX) startX = dx;
                    if (dx > endX) endX = dx;
                }
            }

            if (startX <= endX) {
                context.fill(centerX + startX, centerY + dy, centerX + endX + 1, centerY + dy + 1, color);
            }
        }
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.active && this.visible) {
            if (i == 0 || i == 1) {
                if (this.clicked(d, e)) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());

                    if (i == 0) this.onPress();
                    else this.onAltPress();

                    return true;
                }
            }
        }

        return false;
    }

    public void onAltPress() {
        Minecraft client = Minecraft.getInstance();

        if (client.screen instanceof WheelScreen wheelScreen)
            wheelScreen.altClick(widget);
    }

    @Override
    public void onPress() {
        Minecraft client = Minecraft.getInstance();

        if (client.screen instanceof WheelScreen wheelScreen)
            wheelScreen.click(widget);
    }
}