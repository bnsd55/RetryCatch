package com.github.bnsd55.main;

import com.github.bnsd55.retryCatch.interfaces.CheckedRunnable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Example runnable implements Runnable interface and run() method like any others,
 * when we run it, it will print the current running thread name and
 * it will throw an ArithmeticException when dividing a number by zero.
 */
public class ExampleRunnable implements CheckedRunnable {
    @Override
    public void run() throws Exception {

        // Prints the current running thread name
        System.out.println("Current thread name: " + Thread.currentThread().getName());

        // Tries to divide a number by zero or get an array cell thar does not exist
        // and throws the threw exception
        if ((new Random()).nextBoolean()) {

            for (int i = 0; i < 5; i++) {
                Random rand = new Random();

                // Generate a number between 1 - 8
                int n = rand.nextInt(8) + 1;

                // if the generated number is 2, create an exception
                if (n == 2) {
                    int anException = 1 / 0;
                }
            }
        } else {
            ArrayList<String> exampleStrings = new ArrayList<>();

            exampleStrings.add("a");
            exampleStrings.add("b");
            exampleStrings.add("c");

            // Create an IndexOutOfBounds exception
            String value = exampleStrings.get(1000);
        }

    }
}
