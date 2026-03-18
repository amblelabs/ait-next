package dev.amble.ait.client.renderer;

import dev.amble.ait.common.items.ItemKeychain;
import dev.amble.ait.common.items.tooltips.KeychainTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ClientKeychainTooltip implements ClientTooltipComponent {

    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/background");

    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 18;
    private static final int SLOTS_PER_ROW = 5;

    private final ItemKeychain.KeychainContents contents;

    public ClientKeychainTooltip(KeychainTooltip tooltip) {
        this.contents = tooltip.contents();
    }

    @Override
    public int getHeight() {
        return SLOT_SIZE_Y + BORDER_WIDTH * 2 + MARGIN_Y;
    }

    @Override
    public int getWidth(Font font) {
        return SLOTS_PER_ROW * SLOT_SIZE_X + BORDER_WIDTH * 2;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();

        handleMouseInput(x, y, minecraft);

        guiGraphics.blitSprite(BACKGROUND_SPRITE, x, y, this.getWidth(font), SLOT_SIZE_Y + BORDER_WIDTH * 2);

        for (int col = 0; col < SLOTS_PER_ROW; col++) {
            int slotIndex = col;
            int slotX = x + col * SLOT_SIZE_X + BORDER_WIDTH;
            int slotY = y + BORDER_WIDTH;

            if (slotIndex < contents.size()) {
                boolean isSelected = (slotIndex == this.contents.selectedIndex());
                renderSlotWithItem(slotX, slotY, slotIndex, isSelected, guiGraphics, font);
            } else {
                renderEmptySlot(slotX, slotY, guiGraphics);
            }
        }
    }

    private void renderSlotWithItem(int x, int y, int index, boolean isSelected, GuiGraphics guiGraphics, Font font) {
        ItemStack itemStack = this.contents.getItem(index);

        this.blit(guiGraphics, x, y, Texture.SLOT);

        guiGraphics.renderItem(itemStack, x + BORDER_WIDTH, y + BORDER_WIDTH, index);
        guiGraphics.renderItemDecorations(font, itemStack, x + BORDER_WIDTH, y + BORDER_WIDTH);

        if (isSelected) {
            guiGraphics.fill(x + BORDER_WIDTH, y + BORDER_WIDTH,
                    x + SLOT_SIZE_X - BORDER_WIDTH, y + SLOT_SIZE_Y - BORDER_WIDTH,
                    0x80FFFFFF);
        }
    }

    private void renderEmptySlot(int x, int y, GuiGraphics guiGraphics) {
        this.blit(guiGraphics, x, y, Texture.SLOT);
    }

    private void handleMouseInput(int tooltipX, int tooltipY, Minecraft minecraft) {
        double mouseX = minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth();
        double mouseY = minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight();

        if (minecraft.mouseHandler.isLeftPressed()) {
            for (int col = 0; col < SLOTS_PER_ROW; col++) {
                int slotIndex = col;
                if (slotIndex >= this.contents.size()) continue;

                int slotX = tooltipX + col * SLOT_SIZE_X + BORDER_WIDTH;
                int slotY = tooltipY + BORDER_WIDTH;

                if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE_X &&
                        mouseY >= slotY && mouseY < slotY + SLOT_SIZE_Y) {

                    ItemStack keychain = minecraft.player.getMainHandItem();
                    ItemKeychain.selectSlot(keychain, slotIndex);
                    break;
                }
            }
        }
    }

    private void blit(GuiGraphics guiGraphics, int i, int j, ClientKeychainTooltip.Texture texture) {
        guiGraphics.blitSprite(texture.sprite, i, j, 0, texture.w, texture.h);
    }

    @Environment(EnvType.CLIENT)
    enum Texture {
        BLOCKED_SLOT(ResourceLocation.withDefaultNamespace("container/bundle/blocked_slot"), 18, 20),
        SLOT(ResourceLocation.withDefaultNamespace("container/bundle/slot"), 18, 20);

        public final ResourceLocation sprite;
        public final int w;
        public final int h;

        Texture(final ResourceLocation resourceLocation, final int j, final int k) {
            this.sprite = resourceLocation;
            this.w = j;
            this.h = k;
        }
    }
}