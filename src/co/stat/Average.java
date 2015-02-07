package co.stat;

import co.Sys;

import java.util.List;

/**
 * Calculates average.
 */
public class Average {
	
	/**
	 * Calculates the average of the given series.
	 *
	 * FIXME: long overflow
	 */
	public double calculate(List<Long> data) {
		Sys.assertTrue(data != null);
			
		int count = data.size();
		long sum = 0;
		
		for (int i = 0; i < count; i++) { sum += data.get(i); }
		
		return sum / count;
	}
	
}