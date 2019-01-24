package com.github.bnsd55.retryCatch.interfaces;

/**
 * Checked runnable thar throws an exception
 */
@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
