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
     *
     * Passing in the payload object is just a trick to prevent dead code elimination. See the
     * description of the Task object for more.
     */
    public void record(long arrivalNs, long startNs, long finishNs, Object payload);

    /**
     * Process (calculate, print out, save, etc.) statistics after the benchmark execution is done.
     */
    public void postProcess() throws Exception;
}
