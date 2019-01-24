package com.github.bnsd55.retryCatch.interfaces;

/**
 * Checked callable that throws an exception
 * @param <T> the return type of the callable
 */
@FunctionalInterface
public interface CheckedCallable<T> {
    T call() throws Exception;
}
