package co.task;

import co.Task;
import co.Sys;
import java.util.Random;

/** 
 * A simple task to represent compute intensive tasks.
 *
 * FEATURES:
 *
 * - It calculates the Nth Fibonacci number, where N is chosen randomly between a given minIndex 
 *   and 2 * minIndex. That is: minIndex <= N < 2 * minIndex 
 * - Randomization is used in order to avoid compiler optimization, that might be possible
 *   if the benchmark calculated the same index always.
 *
 * TODO:
 *
 * - Perhaps a smaller interval, ie. [minIndex, minIndex + K] would do a better job.
 */
public class RandomizedFibonacci implements Task {
    private int minIndex;
    private Random random;
    
    /**
     * Initializes the Fibonacci task with the given (minimal) index and modulus.
     */
    public RandomizedFibonacci(int minIndex) {
        Sys.assertTrue(minIndex >= 0);
        
        this.minIndex = minIndex;
        random = new Random();
    }
    
    /**
     * Executes the logic represented by the Task, this case it calculates the Nth Fibonacci number
     * where N is chosen randomly between minIndex and 2 * minIndex.
     */
    @Override
    public Object execute() {
        long fib = 0;
        long index = (long) minIndex + (long) random.nextInt(minIndex);

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
        return "Fibonacci with index in [" + minIndex + ", " + (2 * ((long) minIndex)) + "]";
    }
}