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
 * - The class guards against dead code elimination and constant folding: return value guards
 *   against dead code elimination (see Task interface and Benchmark for more); index field is made
 *   public in order to guard against constant folding.
 * - However since a benchmark will always compute the same number, a very sophisticated compiler
 *   might falsely optimize this code (kinda sophisticated form of constant folding). This case use
 *   the RandomizedFibonacci task.
 *
 *   You can simply test whether the compiler does or not constant folding. Just rerun the task with
 *   different indices. If the execution time remains the same, then you can suspect constant
 *   folding.
 */ 
public class Fibonacci implements Task {
    /**
     * The index value, ie. `execute` will calculate the index'th Fibonacci number. Field is made
     * public in order to guard against constant folding.
     */
    public int index;

    /**
     * Initializes the Fibonacci task with the given index.
     */
    public Fibonacci(int index) {
        Sys.assertTrue(index >= 0);

        this.index = index;
    }

    /**
     * Executes the logic represented by the Task, this case it calculates the Nth Fibonacci number.
     *
     * The return value is just a trick in order to avoid dead code elimination. See the Task
     * interface for more.
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