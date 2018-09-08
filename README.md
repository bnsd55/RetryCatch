# RetryCatch
[![Build Status](https://travis-ci.com/bnsd55/RetryCatch.svg?branch=master)](https://travis-ci.com/bnsd55/RetryCatch)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.bnsd55/RetryCatch/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.bnsd55/RetryCatch)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Open Source Love svg2](https://badges.frapsoft.com/os/v2/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity)

A retry catch mechanism that provides a synchronous and asynchronous running, using Java 8.

Basically, it trying to save your code, if it's broken.

# Installation
Add the latest RetryCatch [Maven](https://search.maven.org/artifact/com.github.bnsd55/RetryCatch/1.0.0/jar) dependency to your project.


# Usage example
For more examples about synchronous and asynchronous retry-catch click [Here](/src/main/java/com/github/bnsd55/main/Main.java).

Instead of runnable you can execute your own anonymous function as executed in the first example [Here](https://github.com/bnsd55/RetryCatch/blob/master/src/main/java/com/github/bnsd55/main/Main.java).

You should not pass any parameters to your anonymous function, if you want to pass parameters, use Callable instead of Runnable or Anonymous functions.

You can use anonymous functions both of synchronous and asynchronous executions.

## Synchronous

### Runnable example
```
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
```

### Callable example
```
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
```

## Asynchronous
Asynchronous running provided by Executors class of java, 
RetryCatch supports both ExecutorService (single thread or thread pool) and ScheduledExecutorService we already know from java 8.

### ExecutorService example

```
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
```

### ScheduledExecutorService example
```
        /**
         * scheduleWithFixedDelay working only with runnable.
         * This scheduleAtFixedRate is acting like the regular scheduleWithFixedDelay
         * method from the ScheduledExecutorService we already know and gets the same parameters.
         */
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
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
```

Enjoy!
