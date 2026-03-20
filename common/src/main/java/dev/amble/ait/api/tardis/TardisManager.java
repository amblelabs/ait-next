package dev.amble.ait.api.tardis;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TardisManager<T extends Tardis> {

    static <T extends Tardis> void accept(Level world, Consumer<TardisManager<T>> consumer) {
        TardisManager<T> manager = get(world);
        if (manager == null) return;

        consumer.accept(manager);
    }

    static <T extends Tardis, R> @Nullable R apply(Level world, Function<TardisManager<T>, R> func) {
        TardisManager<T> manager = get(world);
        if (manager == null) return null;

        return func.apply(manager);
    }

    static <T extends Tardis> TardisManager<T> getOrCreate(Level world) {
        TardisManager<T> result = TardisManager.get(world);
        if (result != null) return result;

        result = TardisManager.<T>asManagerLevel(world).ait$initTardisManager();
        return result;
    }

    static <T extends Tardis> @Nullable TardisManager<T> get(Level world) {
        return TardisManager.<T>asManagerLevel(world).ait$getTardisManager();
    }

    @SuppressWarnings("unchecked")
    static <T extends Tardis> ManagerLevel<T> asManagerLevel(Object world) {
        if (!(world instanceof ManagerLevel<?> level)) {
            CrashReport crashReport = CrashReport.forThrowable(new ClassCastException("Level " + world + " does not implement ManagerLevel!"), "Getting Tardis Manager");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Tardis Manager");
            crashReportCategory.setDetail("Level", world);
            throw new ReportedException(crashReport);
        }

        try {
            return (ManagerLevel<T>) level;
        } catch (ClassCastException e) {
            CrashReport crashReport = CrashReport.forThrowable(new IllegalStateException("Tried getting Tardis Manager with improper type!"), "Getting TardisManager");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Tardis Manager");
            crashReportCategory.setDetail("Level", world);
            crashReportCategory.setDetail("Tardis Manager", level);
            throw new ReportedException(crashReport);
        }
    }

    void remove(UUID id);
    void add(T tardis);

    boolean contains(UUID id);
    @Nullable T get(UUID id);

    interface ManagerLevel<T extends Tardis> {
        TardisManager<T> ait$initTardisManager();
        @Nullable TardisManager<T> ait$getTardisManager();
    }
}
