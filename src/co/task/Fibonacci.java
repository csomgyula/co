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
 * - Randomization is used in order to avoid (JIT) compiler optimization, that might be possible
 *   if the benchmark calculated the same index always.
 */ 
public class Fibonacci implements Task {
    private int minIndex;
    private Random random;
    
    /**
     * Initializes the Fibonacci task with the given (minimal) index and modulus.
     */
    public Fibonacci(int minIndex) {
        Sys.assertTrue(minIndex >= 0);
        
        this.minIndex = minIndex;
        random = new Random();
    }
    
    /**
     * Executes the logic represented by the Task, this case it calculates the Nth Fibonacci number
     * where N is chosen randomly between minIndex and 2 * minIndex.
     */
    @Override
    public void execute() { 
        long fib;
        long index = (long) minIndex + (long) random.nextInt(minIndex);

        if (index > 1) {
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
    }

    @Override
    public String toString() {
        return "Fibonacci with index in [" + minIndex + ", " + (2 * ((long) minIndex)) + "]";
    }
}