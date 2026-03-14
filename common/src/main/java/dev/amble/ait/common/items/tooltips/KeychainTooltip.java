package dev.amble.ait.common.items.tooltips;

import dev.amble.ait.common.items.ItemKeychain;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record KeychainTooltip(ItemKeychain.KeychainContents contents) implements TooltipComponent {
    public List<ItemStack> items() {
        return contents.itemsCopy();
    }

    public int size() {
        return contents.size();
    }
}