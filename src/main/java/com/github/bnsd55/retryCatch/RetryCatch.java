package com.github.bnsd55.retryCatch;

import com.github.bnsd55.retryCatch.interfaces.ExecutorServiceProvider;
import com.github.bnsd55.retryCatch.interfaces.ScheduledExecutorServiceProvider;
import com.github.bnsd55.retryCatch.utilities.Predicates;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This class provides a generic Retry-Catch mechanism,
 * synchronous and asynchronous are supported via ExecutorService.
 */
public class RetryCatch implements ExecutorServiceProvider, ScheduledExecutorServiceProvider {

    /**
     * Retry times.
     * RetryCount gets the absolute value of the integer so
     * there is no meaning to initialize it with negative value.
     */
    private int retryCount;

    /**
     * Indicates the value of infinite retry times.
     * Because the is no meaning to negative values (all negative retry count converted to positive)
     * this indication is negative.
     */
    private int INFINITE_TIMES = -1;

    /**
     * Executes callback when the retry failed.
     * Callable is a consumer with exception
     */
    private Consumer<Exception> failListener;

    /**
     * Executes callback when the retry failed and have not reached to the max retry time.
     * Callback is a BiConsumer with the retry count as integer and the exception
     */
    private BiConsumer<Integer, Exception> retryListener;

    /**
     * Executes callback when the task succeed.
     * Callback is a consumer with the value we expected from the executed callable
     */
    private Consumer successCListener;

    /**
     * Executes callback when the task succeed.
     * Returns nothing because we are not expecting a returned value from a runnable
     */
    private Runnable successRListener;

    /**
     * A predicate that determine if the RetryCatch should keep,
     * processing according to the threw exception type.
     */
    private Predicate<Throwable> retryOnExceptions;

    /**
     * An ExecutorService in order to run asynchronous RetryCatch,
     * ScheduledThreadPool, SingleThreadExecutor and newFixedThreadPool are supported.
     */
    private ExecutorService executorService;

    /**
     * Ctor
     */
    public RetryCatch() {
        this.retryCount = INFINITE_TIMES;
        this.failListener = null;
        this.retryListener = null;
        this.successCListener = null;
        this.successRListener = null;
        this.executorService = null;

        // Initializing RetryCatch to retry on every exception if no exception mentioned
        this.setRetryOnExceptions(Exception.class);
    }

    /**
     * Initialize retry times,
     * gets the absolute value of the integer so
     * there is no meaning to initialize it with negative value,
     * -20 consider as 20 and the RetryCatch will retry the task 20 times.
     *
     * @param count the retry times
     * @return this instance in order to keep initializing
     */
    public RetryCatch retryCount(int count) {
        this.retryCount = Math.abs(count);

        return this;
    }

    /**
     * Executes a callable (synchronous),
     * The RetryCatch core for callable objects,
     * manage the retry times of callable
     *
     * @param callable the function to execute
     * @param <T>      the type of the returned value
     */
    @Override
    public <T> void call(Callable<T> callable) {
        int retries = 0;

        while (true) {
            try {
                T result = callable.call();

                if (this.successCListener != null && this.successRListener != null) {
                    System.err.println("Error: Must be a single onSuccess callback");
                } else if (this.successCListener == null && this.successRListener != null) {
                    System.err.println("Error: onSuccess callback must initialized with a result parameter");
                } else if (this.successCListener != null && this.successRListener == null) {
                    this.successCListener.accept(result);
                }

                break;
            } catch (Throwable t) {
                if (!this.retryCatch(t, retries)) break;
            } finally {
                retries++;
            }
        }
    }

    /**
     * Executes a runnable (synchronous),
     * The RetryCatch core of runnable objects,
     * manage the retry times of runnable
     *
     * @param runnable the task to execute
     */
    @Override
    public void run(Runnable runnable) {
        int retries = 0;

        while (true) {
            try {
                runnable.run();

                if (this.successCListener != null && this.successRListener != null) {
                    System.err.println("Error: Must be a single onSuccess callback");
                } else if (this.successRListener == null && this.successCListener != null) {
                    System.err.println("Error: onSuccess callback cannot initialized with a parameter");
                } else if (this.successRListener != null && this.successCListener == null) {
                    this.successRListener.run();
                }

                break;
            } catch (Throwable t) {
                if (!this.retryCatch(t, retries)) {
                    break;
                }
            } finally {
                if (this.retryCount != INFINITE_TIMES) {
                    retries++;
                }
            }
        }
    }

    /**
     * Executes a runnable (asynchronous) via ExecutorService,
     * executes the runnable with the execute method of the ExecutorService,
     * available only to SingleThreadExecutor and newFixedThreadPool.
     *
     * @param runnable the task to execute
     */
    @Override
    public void execute(Runnable runnable) {
        if (this.executorService != null) {
            if (!(this.executorService instanceof ScheduledExecutorService)) {
                this.executorService.execute(() -> this.run(runnable));
            } else {
                System.err.println("Error: You should use ThreadExecutor or ThreadPoolExecutor in order to use execute() method");
            }
        } else {
            System.err.println("Error: You must create an ExecutorService in order to use execute() method");
        }
    }

    /**
     * Executes a callable (asynchronous) via ExecutorService,
     * executes the callable with the submit method of the ExecutorService,
     * available only to SingleThreadExecutor and newFixedThreadPool.
     *
     * @param callable the unction to execute
     * @param <T>      the type of the returned value
     */
    @Override
    public <T> void submit(Callable<T> callable) {
        if (this.executorService != null) {
            if (!(this.executorService instanceof ScheduledExecutorService)) {
                this.executorService.submit(() -> this.call(callable));
            } else {
                System.err.println("Error: You should use ThreadExecutor or ThreadPoolExecutor in order to use submit() method");
            }
        } else {
            System.err.println("Error: You must create an ExecutorService in order to use submit() method");
        }
    }

    /**
     * Executes a callable (asynchronous) that becomes enabled after the
     * given delay Using schedule method of provided ExecutorService,
     * available only to ScheduledThreadPool.
     *
     * @param callable the function to execute
     * @param delay    the time from now to delay execution
     * @param unit     the time unit of the delay parameter
     * @param <T>      the type of the returned value
     */
    @Override
    public <T> void schedule(Callable<T> callable, long delay, TimeUnit unit) {
        if (this.executorService != null) {
            if (this.executorService instanceof ScheduledExecutorService) {
                ((ScheduledExecutorService) this.executorService).schedule(() -> this.call(callable), delay, unit);
            } else {
                System.err.println("Error: You should use ScheduledExecutorService in order to use schedule() method");
            }
        } else {
            System.err.println("Error: You must create an ScheduledExecutorService in order to use schedule() method");
        }
    }

    /**
     * Executes (asynchronous) a one-shot action (runnable) that becomes enabled
     * after the given delay Using schedule method of provided ExecutorService,
     * available only to ScheduledThreadPool.
     *
     * @param runnable the task to execute
     * @param delay    the time from now to delay execution
     * @param unit     the time unit of the delay parameter
     */
    @Override
    public void schedule(Runnable runnable, long delay, TimeUnit unit) {
        if (this.executorService != null) {
            if (this.executorService instanceof ScheduledExecutorService) {
                ((ScheduledExecutorService) this.executorService).schedule(() -> this.run(runnable), delay, unit);
            } else {
                System.err.println("Error: You should use ScheduledExecutorService in order to use schedule() method");
            }
        } else {
            System.err.println("Error: You must create an ScheduledExecutorService in order to use schedule() method");
        }
    }

    /**
     * Executes a periodic action (runnable) that becomes enabled first
     * after the given initial delay, and subsequently with the given period.
     *
     * @param runnable     the task to execute
     * @param initialDelay the time to delay first execution
     * @param period       the period between successive executions
     * @param unit         the time unit of the initialDelay and period parameters
     */
    @Override
    public void scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        if (this.executorService != null) {
            if (this.executorService instanceof ScheduledExecutorService) {
                ((ScheduledExecutorService) this.executorService).scheduleAtFixedRate(() -> this.run(runnable), initialDelay, period, unit);
            } else {
                System.err.println("Error: You should use ScheduledExecutorService in order to use scheduleAtFixedRate() method");
            }
        } else {
            System.err.println("Error: You must create an ScheduledExecutorService in order to use scheduleAtFixedRate() method");
        }
    }

    /**
     * Executes a periodic action (runnable) that becomes enabled first
     * after the given initial delay, and subsequently with the
     * given delay between the termination of one execution and the
     * commencement of the next.
     *
     * @param runnable     the task to execute
     * @param initialDelay the time to delay first execution
     * @param delay        the delay between the termination of one execution and the commencement of the next
     * @param unit         the time unit of the initialDelay and period parameters
     */
    @Override
    public void scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        if (this.executorService != null) {
            if (this.executorService instanceof ScheduledExecutorService) {
                ((ScheduledExecutorService) this.executorService).scheduleAtFixedRate(() -> this.run(runnable), initialDelay, delay, unit);
            } else {
                System.err.println("Error: You should use ScheduledExecutorService in order to use scheduleWithFixedDelay() method");
            }
        } else {
            System.err.println("Error: You must create an ScheduledExecutorService in order to use scheduleWithFixedDelay() method");
        }
    }

    /**
     * Initializing failed callback to execute when RetryCatch failed
     * with a callable that gets an exception.
     *
     * @param failConsumer the failed callback to execute
     * @return this instance in order to keep initializing
     */
    public RetryCatch onFailure(Consumer<Exception> failConsumer) {
        this.failListener = failConsumer;
        return this;
    }

    /**
     * Initializing retry callback to execute when RetryCatch tries to execute a task
     * after first failure and until ad retry times as initialized before
     *
     * @param retryConsumer the retry callable to execute each retry time
     * @return this instance in order to keep initializing
     */
    public RetryCatch onRetry(BiConsumer<Integer, Exception> retryConsumer) {
        this.retryListener = retryConsumer;
        return this;
    }

    /**
     * Initializing success callback to execute when RetryCatch succeeded with a parameter,
     * this callback will execute when the running object is a callable,
     * the parameter is the returned value from the callable.
     *
     * @param consumer the function to execute when RetryCatch succeeded
     * @param <T>      the type of the returned value
     * @return this instance in order to keep initializing
     */
    public <T> RetryCatch onSuccess(Consumer<T> consumer) {
        this.successCListener = consumer;

        return this;
    }

    /**
     * Initializing success callback to execute when RetryCatch succeeded,
     * this callback will execute when the running object is a runnable.
     *
     * @param runnable the task to execute when RetryCatch succeeded
     * @return this instance in order to keep initializing
     */
    public RetryCatch onSuccess(Runnable runnable) {
        this.successRListener = runnable;

        return this;
    }

    /**
     * Initializing exceptions that RetryCatch should retry on, if no exception provided,
     * Exception.class is the default exception to handle and retry (as mentioned in the Ctor).
     *
     * @param exceptions the exception's classes that RetryCatch should retry when threw
     * @return this instance in order to keep initializing
     */
    @SafeVarargs
    public final RetryCatch retryOn(Class<? extends Throwable>... exceptions) {
        this.setRetryOnExceptions(exceptions);

        return this;
    }

    /**
     * Initializing retryOn predicate with a suitable test in order to know if
     * we should retry the exception that threw while processing the task.
     *
     * @param exceptions the exception's classes we want to retry on
     */
    @SafeVarargs
    private final void setRetryOnExceptions(Class<? extends Throwable>... exceptions) {
        this.retryOnExceptions = Predicates.isExceptionRetryable(exceptions);
    }

    /**
     * Executes the test method of the predicate,
     * If the threw exception has any connection to some exception from our exception's list
     * we will retry the task, otherwise not.
     *
     * @param exception the exception the threw by the running task (runnable or callable)
     * @return true if RetryCatch should keep retry the task, otherwise false
     */
    private boolean isRetryable(Throwable exception) {
        return this.retryOnExceptions.test(exception);
    }

    /**
     * Initializing executor service in order to execute tasks asynchronous
     *
     * @param es  the wanted executor service
     * @param <T> the type of the executor service, for example: ThreadPoolExecutor, SingleThreadExecutor...
     * @return this instance in order to keep initializing
     */
    public <T extends ExecutorService> RetryCatch withExecutor(T es) {
        this.executorService = es;

        return this;
    }

    /**
     * Determined if RetryCatch should keep processing the task,
     * checks if the threw exception is one of the exceptions that RetryCatch should take care
     * and checks if we are not at the max retry count.
     * Executes a retry callback and fail callback when needed.
     *
     * @param exception the threw exception
     * @param retries   the current retry counter
     * @return true if RetryCatch should keep processing, otherwise false
     */
    private boolean retryCatch(Throwable exception, int retries) {
        if (exception instanceof Exception) {
            if (this.isRetryable(exception)) {
                if (this.retryCount == INFINITE_TIMES) {
                    if (this.retryListener != null) {
                        this.retryListener.accept(retries, (Exception) exception);
                    }

                    return true;
                } else if (retries < this.retryCount) {
                    if (this.retryListener != null) {
                        this.retryListener.accept(retries, (Exception) exception);
                    }

                    return true;
                } else if (this.failListener != null) {
                    this.failListener.accept((Exception) exception);
                }
            } else if (this.failListener != null) {
                this.failListener.accept((Exception) exception);
            }
        }

        return false;
    }
}
