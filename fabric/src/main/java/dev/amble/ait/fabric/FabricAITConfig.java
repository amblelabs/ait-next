package dev.amble.liquor.fabric;

import com.google.gson.GsonBuilder;
import dev.amble.liquor.api.LiquorAPI;
import dev.amble.liquor.api.mod.LiquorConfig;
import dev.amble.liquor.xplat.IXplatAbstractions;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.minecraft.resources.ResourceLocation;

@Config(name = LiquorAPI.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/calcite.png")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class FabricLiquorConfig extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.TransitiveObject
    public final Common common = new Common();

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public final Client client = new Client();

    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public final Server server = new Server();

    public static FabricLiquorConfig setup() {
        var gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();
        AutoConfig.register(FabricLiquorConfig.class, PartitioningSerializer.wrap((cfg, clazz) ->
            new GsonConfigSerializer<>(cfg, clazz, gson)));
        var instance = AutoConfig.getConfigHolder(FabricLiquorConfig.class).getConfig();

        LiquorConfig.setCommon(instance.common);

        // We care about the client only on the *physical* client ...
        if (IXplatAbstractions.INSTANCE.isPhysicalClient()) {
            LiquorConfig.setClient(instance.client);
        }
        // but we care about the server on the *logical* server
        // i believe this should Just Work without a guard? assuming we don't access it from the client ever
        LiquorConfig.setServer(instance.server);

        return instance;
    }

    @Config(name = "common")
    public static final class Common implements LiquorConfig.CommonConfigAccess, ConfigData {

        @Override
        public void validatePostLoad() throws ValidationException {
        }
    }

    @Config(name = "client")
    public static final class Client implements LiquorConfig.ClientConfigAccess, ConfigData {

        @Override
        public void validatePostLoad() throws ValidationException {
        }
    }

    @Config(name = "server")
    public static final class Server implements LiquorConfig.ServerConfigAccess, ConfigData {

        @Override
        public void validatePostLoad() throws ValidationException {
        }
    }
}