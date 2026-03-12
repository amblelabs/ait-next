package dev.amble.ait.api.mod;

import dev.amble.ait.api.AitAPI;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class AitConfig {

    public interface CommonConfigAccess {

    }

    public interface ClientConfigAccess {

    }

    public interface ServerConfigAccess {

    }

    public static boolean anyMatch(List<? extends String> keys, ResourceLocation key) {
        for (String s : keys) {
            var rl = ResourceLocation.tryParse(s);

            if (rl != null && rl.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static boolean noneMatch(List<? extends String> keys, ResourceLocation key) {
        return !anyMatch(keys, key);
    }

    @SuppressWarnings("unused")
    public static boolean anyMatchResLoc(List<? extends ResourceLocation> keys, ResourceLocation key) {
        return keys.stream().anyMatch(key::equals);
    }

    // oh man this is aesthetically pleasing
    private static @Nullable CommonConfigAccess common = null;
    private static @Nullable ClientConfigAccess client = null;
    private static @Nullable ServerConfigAccess server = null;

    public static CommonConfigAccess common() {
        return Objects.requireNonNull(common, "accessed config too early");
    }

    public static ClientConfigAccess client() {
        return Objects.requireNonNull(client, "accessed config too early");
    }

    public static ServerConfigAccess server() {
        return Objects.requireNonNull(server, "accessed config too early");
    }

    public static void setCommon(CommonConfigAccess access) {
        if (common != null) {
            AitAPI.LOGGER.warn("CommonConfigAccess was replaced! Old {} New {}",
                common.getClass().getName(), access.getClass().getName());
        }

        common = access;
    }

    public static void setClient(ClientConfigAccess access) {
        if (client != null) {
            AitAPI.LOGGER.warn("ClientConfigAccess was replaced! Old {} New {}",
                client.getClass().getName(), access.getClass().getName());
        }

        client = access;
    }

    public static void setServer(ServerConfigAccess access) {
        if (server != null) {
            AitAPI.LOGGER.warn("ServerConfigAccess was replaced! Old {} New {}",
                server.getClass().getName(), access.getClass().getName());
        }

        server = access;
    }
}