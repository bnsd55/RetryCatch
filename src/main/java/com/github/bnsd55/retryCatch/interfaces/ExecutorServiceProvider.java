package com.github.bnsd55.retryCatch.interfaces;

import java.util.concurrent.Callable;

public interface ExecutorServiceProvider {

    /**
     * @param callable the function to execute
     * @param <T>      the type of the returned value
     * @see Callable#call()
     */
    <T> void call(CheckedCallable<T> callable);

    /**
     * @param callable the function to execute
     * @param <T>      the type of returned value
     * @see java.util.concurrent.ExecutorService#submit(Callable)
     */
    <T> void submit(CheckedCallable<T> callable);

    /**
     * @param runnable the task to execute
     * @see Runnable#run()
     */
    void run(CheckedRunnable runnable);

    /**
     * @param runnable the task to execute
     * @see java.util.concurrent.ExecutorService#execute(Runnable)
     */
    void execute(CheckedRunnable runnable);
}