package dev.amble.ait.api;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public interface AITAPI {
    String MOD_ID = "ait";
    Logger LOGGER = LogManager.getLogger(MOD_ID);

    Supplier<AITAPI> INSTANCE = Suppliers.memoize(() -> {
        try {
            return (AITAPI) Class.forName("dev.amble.ait.common.impl.AITAPIImpl")
                    .getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            LogManager.getLogger().warn("Unable to find AITAPIImpl, using a dummy");
            return new AITAPI() {
            };
        }
    });

    String DRUNKNESS_USERDATA = modLoc("drunkness").toString();

    static AITAPI instance() {
        return INSTANCE.get();
    }

    static ResourceLocation modLoc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
