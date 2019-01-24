package com.github.bnsd55.retryCatch.interfaces;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public interface ScheduledExecutorServiceProvider {
    /**
     * @see java.util.concurrent.ScheduledExecutorService#schedule(Callable, long, TimeUnit)
     */
    <T> void schedule(CheckedCallable<T> callable, long delay, TimeUnit unit);

    /**
     * @see java.util.concurrent.ScheduledExecutorService#schedule(Runnable, long, TimeUnit)
     */
    void schedule(CheckedRunnable command, long delay, TimeUnit unit);

    /**
     * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)
     */
    void scheduleAtFixedRate(CheckedRunnable command, long initialDelay, long period, TimeUnit unit);

    /**
     * @see java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)
     */
    void scheduleWithFixedDelay(CheckedRunnable command, long initialDelay, long delay, TimeUnit unit);
}
