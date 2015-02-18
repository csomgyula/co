package co.task;

import co.Sys;
import co.Task;

import java.util.Random;

/** 
 * A simple task to represent compute intensive tasks.
 *
 * FEATURES:
 *
 * - It calculates the Nth Fibonacci number
 *
 * NOTE:
 *
 * - The class guards against dead code elimination, however since a benchmark will always compute
 *   the same number a very sophisticated compiler might falsely optimize this code (kinda
 *   sophisticated form of constant folding). This case use the RandomizedFibonacci task.
 *
 *   You can simply test whether the compiler does or not constant folding. Just rerun the task with
 *   different indices. If the execution time remains the same, then you can suspect constant
 *   folding.
 */ 
public class Fibonacci implements Task {
    private int index;

    /**
     * Initializes the Fibonacci task with the given (minimal) index and modulus.
     */
    public Fibonacci(int index) {
        Sys.assertTrue(index >= 0);

        this.index = index;
    }

    /**
     * Executes the logic represented by the Task, this case it calculates the Nth Fibonacci number.
     */
    @Override
    public Object execute() {
        long fib = 0;

        // special cases: index = 0 or 1
        if (index == 0) {
            fib = 0;
        }
        else if (index == 1) {
            fib = 1;
        }
        // general case
        else if (index > 1) {
            // initialize the sliding window
            long fibPrev = 0;
            fib = 1;
            long fibTmp;

            // calculate
            for (long i = 2; i <= index; i++){
                fibTmp = fibPrev + fib;
                fibPrev = fib;
                fib = (int) fibTmp;
            }
        }

        //if (Sys.DEBUG) { Sys.debug("fibonacci["+minIndex+"] % " + modulus + " = " + fib); }

        return fib;
    }

    @Override
    public String toString() {
        return index + "th Fibonacci number";
    }
}