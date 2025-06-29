package fr.kayrouge.athena.common.util.compat;

import fr.kayrouge.athena.common.util.CPlatform;
import fr.kayrouge.athena.common.util.exception.WrongCallException;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class TaskCompat {

    public static BukkitTask runTask(Runnable task) {
        return Bukkit.getScheduler().runTask(PlatformCompat.INSTANCE, task);
    }

    public static void runTask(Consumer<BukkitTask> task) {
        Bukkit.getScheduler().runTask(PlatformCompat.INSTANCE, task);
    }

    public static void runAsyncTask(Consumer<SimpleTask> task) {
        if(PlatformCompat.isPaper) {
            Bukkit.getAsyncScheduler().runNow(PlatformCompat.INSTANCE, scheduledTask -> {
                task.accept(SimpleTask.of(scheduledTask));
            });
        }
        else {
            Bukkit.getScheduler().runTaskAsynchronously(PlatformCompat.INSTANCE, bukkitTask -> {
                task.accept(SimpleTask.of(bukkitTask));
            });
        }
    }

    public record SimpleTask(CPlatform platform,
    /* BUKKIT TASK */        boolean isSync, int taskID,
    /* COMMON TASK */        boolean isCancelled, Plugin owner,
    /* PAPER TASK  */        boolean isRepeatingTask, @Nullable ScheduledTask.ExecutionState executionState,
                             Runnable cancel, Object originalTask) {


        public static SimpleTask of(ScheduledTask task) {
            return new SimpleTask(CPlatform.PAPER, false, -1,
                    task.isCancelled(), task.getOwningPlugin(), task.isRepeatingTask(), task.getExecutionState(),
                    task::cancel, task);
        }

        public static SimpleTask of(BukkitTask task) {
            return new SimpleTask(CPlatform.BUKKIT, task.isSync(), task.getTaskId(),
                    task.isCancelled(), task.getOwner(), false, null,
                    task::cancel, task);
        }

        public BukkitTask getBukkitTask() {
            if(platform() == CPlatform.PAPER) throw new WrongCallException("Can't cast a ScheduledTask to a BukkitTask !");
            return (BukkitTask) originalTask();
        }

        public ScheduledTask getPaperTask() {
            if(platform() == CPlatform.BUKKIT) throw new WrongCallException("Can't cast a BukkitTask to a ScheduledTask !");
            return (ScheduledTask) originalTask();
        }

    }

}
