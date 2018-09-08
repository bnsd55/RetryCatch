package com.github.bnsd55.main;

import com.github.bnsd55.retryCatch.RetryCatch;

import java.nio.file.FileAlreadyExistsException;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    public static void main(String... args) {

        /**  =================================================
         *   ================== Synchronous ==================
         *   =================================================
         *
         *   Those examples are running from the called thread
         */

        /**
         * Will retry to run ExampleRunnable 3 times, each time one of ArithmeticException,
         * FileAlreadyExistsException or IndexOutOfBoundsException are threw.
         * If the runnable threw an exception that does not exists in the retryOn list it will not retry the task.
         * Because there is not return value from Runnable, the success callback does not have a returned parameter.
         * We also can change the retryOn exception to Exception.class in order to retry on every threw exception.
         */
        RetryCatch retryCatchSyncRunnable = new RetryCatch();
        retryCatchSyncRunnable
                // For infinite retry times, just remove this row
                .retryCount(3)
                // For retrying on all exceptions, just remove this row
                .retryOn(ArithmeticException.class, IndexOutOfBoundsException.class)
                .onSuccess(() -> System.out.println("Success, There is no result because this is a runnable."))
                .onRetry((retryCount, e) -> System.out.println("Retry count: " + retryCount + ", Exception message: " + e.getMessage()))
                .onFailure(e -> System.out.println("Failure: Exception message: " + e.getMessage()))
                .run(new ExampleRunnable());


        /**
         * Will retry to run the callable 3 times, each time that a = 0, it will divide b by zero
         * and throw an ArithmeticException. Because we've mentioned ArithmeticException.class in
         * retryOn's list, it will retry the task maximum 3 times until it gets success flag.
         */
        RetryCatch retryCatchSyncCallable = new RetryCatch();
        retryCatchSyncCallable
                // For infinite retry times, just remove this row
                .retryCount(3)
                // For retrying on all exceptions, just remove this row
                .retryOn(ArithmeticException.class)
                .onSuccess((result) -> System.out.println("Success, Result: " + result))
                .onRetry((retryCount, e) -> System.out.println("Retry count: " + retryCount + ", Exception message: " + e.getMessage()))
                .onFailure(e -> System.out.println("Failure: Exception message: " + e.getMessage()))
                .call(new Callable<Integer>() {
                    public Integer call() {
                        Random random = new Random();

                        // Generate number between 0 - 2
                        int a = random.nextInt(2) + 0;
                        int b = 10;

                        // Sometimes (when a=0) this will throw an Arithmetic Exception (divided by zero)
                        return b / a;
                    }
                });

        /**  ==================================================
         *   ================== Asynchronous ==================
         *   ==================================================
         *
         *   Those examples are running on a different thread
         *   provided by java Executors.
         */

        /**
         * Executor Service: Single Thread and Thread Pool
         *
         * Actually, there is no difference between thread pool and single thread execution except
         * the executor object.
         */
        ExecutorService singleThread = Executors.newSingleThreadExecutor();
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);

        /**
         * Same as synchronous runnable but this one will run on a different thread provided by
         * Executors class of java, on a single thread.
         * In this case, we use the withExecutor() method to mention the executor we want to and
         * we run the task via execute() method that fires the runnable on a thread provided by Executors.
         */
        RetryCatch retryCatchAsyncRunnableSingleThread = new RetryCatch();
        retryCatchAsyncRunnableSingleThread
                // For infinite retry times, just remove this row
                .retryCount(3)
                // For retrying on all exceptions, just remove this row
                .retryOn(ArithmeticException.class, IndexOutOfBoundsException.class)
                .onSuccess(() -> System.out.println("Success, There is no result because this is a runnable."))
                .onRetry((retryCount, e) -> System.out.println("Retry count: " + retryCount + ", Exception message: " + e.getMessage()))
                .onFailure(e -> System.out.println("Failure: Exception message: " + e.getMessage()))
                .withExecutor(singleThread) // or threadPoolExecutor object
                .execute(new ExampleRunnable());

        /**
         * Same as synchronous callable but this one will run on a different thread provided by
         * Executors class of java, on a single thread.
         * In this case, we use the withExecutor() method to mention the executor we want to and
         * we run the task via submit() method that fires the callable on a thread provided by Executors.
         */
        RetryCatch retryCatchAsyncCallableSingleThread = new RetryCatch();
        retryCatchAsyncCallableSingleThread
                // For infinite retry times, just remove this row
                .retryCount(3)
                // For retrying on all exceptions, just remove this row
                .retryOn(ArithmeticException.class)
                .onSuccess((result) -> System.out.println("Success, Result: " + result))
                .onRetry((retryCount, e) -> System.out.println("Retry count: " + retryCount + ", Exception message: " + e.getMessage()))
                .onFailure(e -> System.out.println("Failure: Exception message: " + e.getMessage()))
                .withExecutor(singleThread) // or threadPoolExecutor object
                .submit(new Callable<Integer>() {
                    public Integer call() {
                        Random random = new Random();

                        // Generate number between 0 - 2
                        int a = random.nextInt(2) + 0;
                        int b = 10;

                        // Sometimes (when a=0) this will throw an Arithmetic Exception (divided by zero)
                        return b / a;
                    }
                });


        /**
         * Scheduled Executor Service
         *
         * Pay attention:
         * scheduleAtFixedRate and scheduleWithFixedDelay are working with runnable and
         * callable can not by executed!
         */
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(5);

        /**
         * Schedule is acting like the regular schedule method from the ScheduledExecutorService
         * we already know.
         * Schedule working with callable and runnable, this example is with runnable,
         * to use callable, read the comment above the schedule method.
         */
        RetryCatch retryCatchAsyncScheduled = new RetryCatch();
        retryCatchAsyncScheduled
                // For infinite retry times, just remove this row
                .retryCount(3)
                // For retrying on all exceptions, just remove this row
                .retryOn(ArithmeticException.class, IndexOutOfBoundsException.class)
                .onSuccess(() -> System.out.println("Success, There is no result because this is a runnable."))
                .onRetry((retryCount, e) -> System.out.println("Retry count: " + retryCount + ", Exception message: " + e.getMessage()))
                .onFailure(e -> System.out.println("Failure: Exception message: " + e.getMessage()))
                .withExecutor(scheduledExecutorService)
                // For callable, just replace the first argument with your callable and do not forget to change onSuccess
                // callable to get the result from your callable
                .schedule(new ExampleRunnable(), 5, TimeUnit.SECONDS);

        /**
         * As mentioned earlier, scheduleAtFixedRate working only with runnable.
         * This scheduleAtFixedRate is acting like the regular scheduleAtFixedRate
         * method from the ScheduledExecutorService we already know and gets the same parameters.
         */
        RetryCatch retryCatchAsyncScheduleAtFixedRate = new RetryCatch();
        retryCatchAsyncScheduleAtFixedRate
                // For infinite retry times, just remove this row
                .retryCount(3)
                // For retrying on all exceptions, just remove this row
                .retryOn(ArithmeticException.class, FileAlreadyExistsException.class, IndexOutOfBoundsException.class)
                .onSuccess(() -> System.out.println("Success, There is no result"))
                .onRetry((retryCount, e) -> System.out.println("Retry count: " + retryCount + ", Exception message: " + e.getMessage()))
                .onFailure(e -> System.out.println("Failure: Exception message: " + e.getMessage()))
                .withExecutor(scheduledExecutorService)
                .scheduleAtFixedRate(new ExampleRunnable(), 1, 2, TimeUnit.SECONDS); // Do not forget, scheduleAtFixedRate does not working with callable.

        /**
         * As mentioned earlier, scheduleWithFixedDelay working only with runnable.
         * This scheduleAtFixedRate is acting like the regular scheduleWithFixedDelay
         * method from the ScheduledExecutorService we already know and gets the same parameters.
         */
        RetryCatch retryCatchAsyncScheduleWithFixedDelay = new RetryCatch();
        retryCatchAsyncScheduleWithFixedDelay
                // For infinite retry times, just remove this row
                .retryCount(3)
                // For retrying on all exceptions, just remove this row
                .retryOn(ArithmeticException.class, FileAlreadyExistsException.class, IndexOutOfBoundsException.class)
                .onSuccess(() -> System.out.println("Success, There is no result"))
                .onRetry((retryCount, e) -> System.out.println("Retry count: " + retryCount + ", Exception message: " + e.getMessage()))
                .onFailure(e -> System.out.println("Failure: Exception message: " + e.getMessage()))
                .withExecutor(scheduledExecutorService)
                // Do not forget, scheduleWithFixedDelay does not working with callable.
                .scheduleWithFixedDelay(new ExampleRunnable(), 5, 5, TimeUnit.SECONDS);

        // Shutdown all the executors we've created
        singleThread.shutdown();
        threadPoolExecutor.shutdown();
        scheduledExecutorService.shutdown();

    }
}
