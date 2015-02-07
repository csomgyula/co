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
 * - In order to control integer overflow it calculates only the remainder by the given modulus. 
 */ 
public class Fibonacci implements Task {
	private int minIndex, modulus;
	private Random random;
	
	/**
	 * Initializes the Fibonacci task with the given (minimal) index and modulus.
	 */
	public Fibonacci(int minIndex, int modulus) {
		Sys.assertTrue(minIndex >= 0 && modulus > 1);
		
		this.minIndex = minIndex;
		this.modulus = modulus;
		this.random = new Random();
	}
	
	/**
	 * Executes the logic represented by the Task, this case it calculates the remainder of the
	 * Nth Fibonacci number, where N is chosen randomly between minIndex and 2 * minIndex.
	 */
	@Override
	public void execute() { 
		int fib;
		long index = minIndex + random.nextInt(minIndex);
		
		// first the special cases: minIndex = 0 or 1
		if (index == 0) { fib = 0; } else if (index == 1) { fib = 1; }
		
		// then the normal cases: minIndex > 1
		else {
			// initialize the sliding window
			int fibPrev = 0;
			fib = 1;
			long fibTmp;
			
			// calculate
			for (int i = 2; i <= index; i++){
				fibTmp = (fibPrev + fib) % modulus;
				fibPrev = fib;
				fib = (int) fibTmp;
			}
		}
		
		//if (Sys.DEBUG) { Sys.debug("fibonacci["+minIndex+"] % " + modulus + " = " + fib); }
	}
}