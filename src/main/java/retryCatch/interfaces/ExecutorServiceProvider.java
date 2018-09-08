package retryCatch.interfaces;

import java.util.concurrent.Callable;

public interface ExecutorServiceProvider {

    /**
     * @param callable the function to execute
     * @param <T>      the type of the returned value
     * @see Callable#call()
     */
    <T> void call(Callable<T> callable);

    /**
     * @param callable the function to execute
     * @param <T>      the type of returned value
     * @see java.util.concurrent.ExecutorService#submit(Callable)
     */
    <T> void submit(Callable<T> callable);

    /**
     * @param runnable the task to execute
     * @see Runnable#run()
     */
    void run(Runnable runnable);

    /**
     * @param runnable the task to execute
     * @see java.util.concurrent.ExecutorService#execute(Runnable)
     */
    void execute(Runnable runnable);
}