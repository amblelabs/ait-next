package dev.amble.ait.api.mod;

import dev.amble.ait.api.AI;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class AITConfig {

    public interface CommonConfigAccess {

    }

    public interface ClientConfigAccess {

    }

    public interface ServerConfigAccess {

    }

    // Simple extensions for resource location configs
    public static boolean anyMatch(List<? extends String> keys, ResourceLocation key) {
        for (String s : keys) {
            if (ResourceLocation.isValidResourceLocation(s)) {
                var rl = new ResourceLocation(s);
                if (rl.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean noneMatch(List<? extends String> keys, ResourceLocation key) {
        return !anyMatch(keys, key);
    }

    public static boolean anyMatchResLoc(List<? extends ResourceLocation> keys, ResourceLocation key) {
        return keys.stream().anyMatch(key::equals);
    }

    // oh man this is aesthetically pleasing
    private static CommonConfigAccess common = null;
    private static ClientConfigAccess client = null;
    private static ServerConfigAccess server = null;

    public static CommonConfigAccess common() {
        return common;
    }

    public static ClientConfigAccess client() {
        return client;
    }

    public static ServerConfigAccess server() {
        return server;
    }

    public static void setCommon(CommonConfigAccess access) {
        if (common != null) {
            AITAPI.LOGGER.warn("CommonConfigAccess was replaced! Old {} New {}",
                common.getClass().getName(), access.getClass().getName());
        }

        common = access;
    }

    public static void setClient(ClientConfigAccess access) {
        if (client != null) {
            AITAPI.LOGGER.warn("ClientConfigAccess was replaced! Old {} New {}",
                client.getClass().getName(), access.getClass().getName());
        }

        client = access;
    }

    public static void setServer(ServerConfigAccess access) {
        if (server != null) {
            AITAPI.LOGGER.warn("ServerConfigAccess was replaced! Old {} New {}",
                server.getClass().getName(), access.getClass().getName());
        }

        server = access;
    }
}