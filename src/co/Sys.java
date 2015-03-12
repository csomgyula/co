package co;

import java.util.concurrent.TimeUnit;

/**
 * A utility class.
 *
 * FEATURES:
 *
 *  - Handles 'time zero' a time offset in order to shorten time stamps 
 *  - Provides a simple debugging facility
 *  - Provides a simple assertion facility
 *
 *  TODO:
 *
 *  - Implement a simple factory to instantiate Load and Task objects
 */
public class Sys{
    public final static long TIME_ZERO;
    public final static boolean DEBUG;
    public static boolean PRINTOUT;

    static { 
        TIME_ZERO = System.nanoTime();
        DEBUG = "t".equals(System.getProperty("debug"));
        PRINTOUT = true;
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
     * Prints out the message if PRINTOUT is true, otherwise skip.
     */
    public static void printlnOut(String message) {
        if (PRINTOUT) {
            System.out.println(message);
        }
    }

    /**
     * Prints out the message if PRINTOUT is true, otherwise skip.
     */
    public static void printOut(String message) {
        if (PRINTOUT) {
            System.out.print(message);
        }
    }

    /**
     * Checks whether the given constraint is true, throws exception if not.
     */
    public static void assertTrue(boolean constraint) {
        if (!constraint) throw new AssertionError();
    }
}