package com.github.bnsd55.retryCatch.utilities;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Utility for creating predicates
 */
public class Predicates {

    /**
     * Creates a predicate that evaluates whether the threw exception should be retried
     *
     * @param exceptions the exception's classes we want to retry on
     * @return predicate that indicates if RetryCatch should retry one more time
     */
    @SafeVarargs
    public static Predicate<Throwable> isExceptionRetryable(Class<? extends Throwable>... exceptions) {
        return t -> {
            if (t == null) {
                return false;
            }

            for (Class<? extends Throwable> failureType : Arrays.asList(exceptions)) {
                if (failureType.isAssignableFrom(t.getClass())) {
                    return true;
                }
            }

            return false;
        };
    }
}
