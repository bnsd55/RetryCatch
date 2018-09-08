# RetryCatch
A retry catch mechanism that provides a synchronous and asynchronous running, using Java 8.

Basically, it trying to save your code, if it's broken.

# Installation
Add the latest RetryCatch [Maven](https://search.maven.org/artifact/com.github.bnsd55/RetryCatch/1.0.0/jar) dependency to your project.


# Usage example
For more examples about synchronous and asynchronous retry-catch click [Here](/src/main/java/com/github/bnsd55/main/Main.java)

## Synchronous

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
