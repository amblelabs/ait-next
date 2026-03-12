package dev.amble.ait.client.renderer;

import dev.amble.ait.common.items.components.SonicCrystals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;

@Environment(EnvType.CLIENT)
public class ClientSonicTooltip implements ClientTooltipComponent {

	private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/background");

	private static final int MARGIN_Y = 4;
	private static final int BORDER_WIDTH = 1;
	private static final int SLOT_SIZE_X = 18;
	private static final int SLOT_SIZE_Y = 20;

	private final SonicCrystals contents;

	public ClientSonicTooltip(SonicCrystals bundleContents) {
		this.contents = bundleContents;
	}

	@Override
	public int getHeight() {
		return this.backgroundHeight() + MARGIN_Y;
	}

	@Override
	public int getWidth(Font font) {
		return this.backgroundWidth();
	}

	private int backgroundWidth() {
		return this.gridSizeX() * SLOT_SIZE_X + BORDER_WIDTH * 2;
	}

	private int backgroundHeight() {
		return this.gridSizeY() * SLOT_SIZE_Y + BORDER_WIDTH * 2;
	}

	@Override
	public void renderImage(Font font, int i, int j, GuiGraphics guiGraphics) {
		int k = this.gridSizeX();
		int l = this.gridSizeY();
		guiGraphics.blitSprite(BACKGROUND_SPRITE, i, j, this.backgroundWidth(), this.backgroundHeight());
		boolean bl = this.contents.weight().compareTo(Fraction.ONE) >= 0;
		int m = 0;

		for (int n = 0; n < l; n++) {
			for (int o = 0; o < k; o++) {
				int p = i + o * SLOT_SIZE_X + BORDER_WIDTH;
				int q = j + n * SLOT_SIZE_Y + BORDER_WIDTH;
				this.renderSlot(p, q, m++, bl, guiGraphics, font);
			}
		}
	}

	private void renderSlot(int i, int j, int k, boolean bl, GuiGraphics guiGraphics, Font font) {
		if (k >= this.contents.size()) {
			this.blit(guiGraphics, i, j, bl ? ClientSonicTooltip.Texture.BLOCKED_SLOT : ClientSonicTooltip.Texture.SLOT);
		} else {
			ItemStack itemStack = this.contents.getItemUnsafe(k);
			this.blit(guiGraphics, i, j, ClientSonicTooltip.Texture.SLOT);
			guiGraphics.renderItem(itemStack, i + BORDER_WIDTH, j + BORDER_WIDTH, k);
			guiGraphics.renderItemDecorations(font, itemStack, i + BORDER_WIDTH, j + BORDER_WIDTH);
			if (k == 0) {
				AbstractContainerScreen.renderSlotHighlight(guiGraphics, i + BORDER_WIDTH, j + BORDER_WIDTH, 0);
			}
		}
	}

	private void blit(GuiGraphics guiGraphics, int i, int j, ClientSonicTooltip.Texture texture) {
		guiGraphics.blitSprite(texture.sprite, i, j, 0, texture.w, texture.h);
	}

	private int gridSizeX() {
		return Math.max(2, (int)Math.ceil(Math.sqrt(this.contents.size() + (float) BORDER_WIDTH)));
	}

	private int gridSizeY() {
		return (int)Math.ceil((this.contents.size() + (float) BORDER_WIDTH) / this.gridSizeX());
	}

	@Environment(EnvType.CLIENT)
	enum Texture {
		BLOCKED_SLOT(ResourceLocation.withDefaultNamespace("container/bundle/blocked_slot"), 18, 20),
		SLOT(ResourceLocation.withDefaultNamespace("container/bundle/slot"), 18, 20);

		public final ResourceLocation sprite;
		public final int w;
		public final int h;

		@SuppressWarnings("SameParameterValue")
        Texture(final ResourceLocation resourceLocation, final int j, final int k) {
			this.sprite = resourceLocation;
			this.w = j;
			this.h = k;
		}
	}
}
