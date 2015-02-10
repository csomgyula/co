package co.load;

import co.Load;
import java.util.concurrent.TimeUnit;

/**
 * Represents a load generator with steady load.
 */
public class Steady implements Load {
    private final long timeDiffNs;
                      
    /** 
     * Initializes the load generator with the given time given in milliseconds. 
     * The given time should represent the steady time between two adjacent requests.
     */
    public Steady(long timeDiffMs) {
        this.timeDiffNs = TimeUnit.NANOSECONDS.convert(timeDiffMs, TimeUnit.MILLISECONDS);
    }
    
    /** 
     * Initializes the load generator with the given time given and time unit.
     * The given time should represent the steady time between two adjacent requests.
     */
    public Steady(long timeDiff, TimeUnit unit) {
        this.timeDiffNs = TimeUnit.NANOSECONDS.convert(timeDiff, unit);
    }
    
    /** 
     * Returns the relative time of the next arrival (in nanoseconds precision), that is the time 
     * between two consecutive requests. This load generator will always return the same constant
     * time this load generator is initialized to.
     */
    @Override
    public long nextRelativeTimeNs(){ return timeDiffNs; }
    
    @Override
    public String toString() {
        return "Steady load with " + ((double) timeDiffNs / 1_000_000d) + " ms frequency";
    }
}