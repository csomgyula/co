package co;

/**
 * Represents the statistical logic.
 *
 * FEATURES:
 *
 * - Records timings (recorded by the processing thread(s))
 * - Process (calculate, print out, save, etc.) statistics
 */
public interface Stat {

    /**
     * Record the given times: the arrival of the request and the start/finish time of its
     * processing.
     */
    public void record(long arrivalNs, long startNs, long finishNs);

    /**
     * Process (calculate, print out, save, etc.) statistics after the benchmark execution is done.
     */
    public void process() throws Exception;
}
