package dev.amble.ait.client.renderer;

import dev.amble.ait.common.I18n;
import dev.amble.ait.common.lib.AitItems;
import dev.amble.ait.xplat.IXplatAbstractions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class AitAdditionalRenderers {

    private static final ItemStack ITEM_ICON = new ItemStack(AitItems.SHARD_GRAVITY);

    @SuppressWarnings("unused")
    public static void overlayGui(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!minecraft.options.hideGui && IXplatAbstractions.INSTANCE.isUnstable()) {
            Font font = Minecraft.getInstance().font;
            graphics.drawStringWithBackdrop(font, I18n.OVERLAY_UNSTABLE, 16 + 4 + 2, 8, 1, 0xffffff);
            graphics.renderFakeItem(ITEM_ICON, 4, 4);
        }
    }
}
