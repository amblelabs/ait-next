package dev.amble.ait.client.renderer;

import dev.amble.ait.client.model.CoralSonicGeoModel;
import dev.amble.ait.common.items.ItemSonic;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CoralSonicItemRenderer extends GeoItemRenderer<ItemSonic> {

    public CoralSonicItemRenderer() {
        super(new CoralSonicGeoModel());
        addRenderLayer(new CoralSonicEmissiveLayer(this));
    }
}

