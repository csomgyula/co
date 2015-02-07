package co;

import java.util.concurrent.TimeUnit;

/**
 * A utility class.
 *
 * FEATURES:
 *
 *  - Handles 'time zero' a time offset in order to shorten time stamps 
 *  - Provides simple debugging facility
 *  - Provides simple assertion
 *
 * TODO:
 *  - Implement assertion logic
 */
public class Sys{
    public final static long TIME_ZERO;
    public final static boolean DEBUG;

    static { 
        TIME_ZERO = System.nanoTime();
        DEBUG = "t".equals(System.getProperty("debug"));
    }
    
    /** 
     * Called just to load this class, hence to initialize time zero.
     */
    public static void timeZero() { Sys.debug("TIME ZERO: " + TIME_ZERO); }
    
    /** 
     * Prints a debug message to the standard console (inspired by syslog): 
     *
     *   <PRIORITY>VERSION TIMESTAMP THREAD [MESSAGE]
     */
    public static void debug(String message) {
        if (DEBUG) {
            long nowNs = System.nanoTime() - TIME_ZERO;
            long now = TimeUnit.MILLISECONDS.convert(nowNs, TimeUnit.NANOSECONDS);
            System.out.println( "<D>1 " + now + " " + Thread.currentThread().getId() + 
                " [" + message + "]");
        }
    }
    
    /**
     * Checks whether the given constraint is true, throws exception if not.
     */
    public static void assertTrue(boolean constraint) {}
}