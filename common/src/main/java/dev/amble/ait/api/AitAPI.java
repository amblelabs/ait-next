package dev.amble.ait.api;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public interface AitAPI {
    String MOD_ID = "ait";
    Logger LOGGER = LogManager.getLogger(MOD_ID);

    Supplier<AitAPI> INSTANCE = Suppliers.memoize(() -> {
        try {
            return (AitAPI) Class.forName("dev.amble.ait.common.impl.AitAPIImpl")
                    .getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            LogManager.getLogger().warn("Unable to find AitAPIImpl, using a dummy");
            return new AitAPI() {
            };
        }
    });

    static AitAPI instance() {
        return INSTANCE.get();
    }

    static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
