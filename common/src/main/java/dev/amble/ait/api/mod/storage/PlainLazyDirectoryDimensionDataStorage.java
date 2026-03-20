package dev.amble.ait.api.mod.storage;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Plain-text Lazy Directory Dimension Data Storage
 */
public class PlainLazyDirectoryDimensionDataStorage {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final DataFixer fixerUpper;
    private final HolderLookup.Provider registries;
    private final File dataFolder;

    public PlainLazyDirectoryDimensionDataStorage(File dataFolder, DataFixer fixerUpper, HolderLookup.Provider registries) {
        this.fixerUpper = fixerUpper;
        this.dataFolder = dataFolder;
        this.registries = registries;
    }

    public static PlainLazyDirectoryDimensionDataStorage get(MinecraftServer server) {
        return get(server.overworld());
    }

    public static PlainLazyDirectoryDimensionDataStorage get(ServerLevel world) {
        if (!(world.getDataStorage() instanceof Provider level)) {
            CrashReport crashReport = CrashReport.forThrowable(new ClassCastException("Level " + world + " does not implement PlainLazyDirectoryDimensionDataStorage!"), "Getting Level Data Storage");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Data Storage");
            crashReportCategory.setDetail("Level", world);
            throw new ReportedException(crashReport);
        }

        return level.ait$getOrCreate();
    }

    private File getDataFile(String name) {
        return new File(this.dataFolder, name + ".nbt");
    }

    public @Nullable CompoundTag readSavedData(String filename, int defaultVersion) {
        try {
            File file = this.getDataFile(filename);

            if (file.exists()) {
                CompoundTag compoundTag = this.readTagFromDisk(filename);
                return Objects.requireNonNull(compoundTag, "read tag must not be null");
            }
        } catch (Exception exception) {
            LOGGER.error("Error loading saved data: {}", filename, exception);
        }

        return null;
    }

    private CompoundTag readTagFromDisk(String filename) throws IOException {
        File file = this.getDataFile(filename);

        try (
                InputStream inputStream = new FileInputStream(file)
//                PushbackInputStream pushbackInputStream = new PushbackInputStream(new FastBufferedInputStream(inputStream), 2);
        ) {
            // FIXME: this is very inefficient. Instead of reading the entire (perhaps malformed) InputStream we can use the stuff above.
            return TagParser.parseTag(new String(inputStream.readAllBytes()));
        } catch (CommandSyntaxException e) {
            throw new NbtFormatException(e.getMessage());
        }
    }

    public void save(String name, CompoundTag tag) {
        try {
            // Again, not great. See NbtIo#SYNC_OUTPUT_OPTIONS
            Files.writeString(this.getDataFile(name).toPath(), tag.toString());
        } catch (IOException iOException) {
            LOGGER.error("Could not save data {}", this, iOException);
        }
    }

    public interface Provider {
        PlainLazyDirectoryDimensionDataStorage ait$getOrCreate();
    }
}
