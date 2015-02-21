package co;

/**
 * Represents the load generator which determines when new requests arrive.
 *
 * FEATURES:
 *
 * - Should return the time diff between the current request and the next one
 *
 * RELATIONS:
 *
 * - The Load interface is called by the Benchmark when scheduling the next execution.
 */
public interface Load {

    /**
      * Returns the relative time of the next arrival (in nanoseconds precision), that is the time
      * between two consecutive requests.
      */
    public long nextRelativeTimeNs();
}