package dev.amble.ait.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.amble.ait.api.AitAPI;
import dev.amble.ait.api.mod.AitConfig;
import dev.amble.ait.xplat.IXplatAbstractions;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.minecraft.resources.ResourceLocation;

@Config(name = AitAPI.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/calcite.png")
public class FabricAitConfig extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.TransitiveObject
    public final Common common = new Common();

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public final Client client = new Client();

    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public final Server server = new Server();

    public static FabricAitConfig setup() {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();

        AutoConfig.register(FabricAitConfig.class, PartitioningSerializer.wrap((cfg, clazz) ->
            new GsonConfigSerializer<>(cfg, clazz, gson)));

        FabricAitConfig instance = AutoConfig.getConfigHolder(FabricAitConfig.class).getConfig();

        AitConfig.setCommon(instance.common);

        if (IXplatAbstractions.INSTANCE.isPhysicalClient()) {
            AitConfig.setClient(instance.client);
        }

        AitConfig.setServer(instance.server);
        return instance;
    }

    @Config(name = "common")
    public static final class Common implements AitConfig.CommonConfigAccess, ConfigData {

        @Override
        public void validatePostLoad() {
        }
    }

    @Config(name = "client")
    public static final class Client implements AitConfig.ClientConfigAccess, ConfigData {

        @Override
        public void validatePostLoad() {
        }
    }

    @Config(name = "server")
    public static final class Server implements AitConfig.ServerConfigAccess, ConfigData {

        @Override
        public void validatePostLoad() {
        }
    }
}