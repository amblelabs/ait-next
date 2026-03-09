package dev.amble.ait.common.items.tooltips;

import dev.amble.ait.common.items.components.SonicCrystals;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record SonicTooltip(SonicCrystals contents) implements TooltipComponent {
}