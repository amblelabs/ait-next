package dev.amble.ait.client.renderer;

import dev.amble.ait.common.I18n;
import dev.amble.ait.xplat.IXplatAbstractions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class AitAdditionalRenderers {

    @SuppressWarnings({"EmptyMethod", "unused"})
    public static void overlayGui(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.options.hideGui && IXplatAbstractions.INSTANCE.isUnstable())
            graphics.drawStringWithBackdrop(Minecraft.getInstance().font, I18n.UNSTABLE_OVERLAY, 8, 8, 1, 0xffffff);
    }
}
