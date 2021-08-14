package Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Utils.Scheduler that can execute runnable method every N seconds
 */
public class Scheduler {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
    ScheduledFuture<?> scheduledFuture;
    boolean isRunning = false;

    /**
     * Start periodic method
     * @param method the method to run
     * @param delay the delay before  first execution
     * @param period period between successive executions
     * @param unit the time unit of the initialDelay and period parameters
     */
    public void startRunnable(Runnable method, int delay, long period, TimeUnit unit) {
        scheduledFuture = executor.scheduleAtFixedRate(method, delay, period, unit);
        isRunning = true;
        System.out.println("Star runnable");
    }

    /**
     * Stop executed method
     */
    public void stopRunnable() {
        scheduledFuture.cancel(false);
        isRunning = false;
        System.out.println("Stop runnable");
    }


}
