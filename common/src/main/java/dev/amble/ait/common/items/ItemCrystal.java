package dev.amble.ait.common.items;

import dev.amble.ait.common.sonic.SonicCrystal;
import net.minecraft.world.item.Item;

public class ItemCrystal extends Item {

    private final SonicCrystal crystal;

    public ItemCrystal(Properties properties, SonicCrystal crystal) {
        super(properties);

        this.crystal = crystal;
    }

    public SonicCrystal getCrystal() {
        return crystal;
    }
}
