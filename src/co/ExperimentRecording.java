package co;


import java.util.ArrayList;
import java.util.List;

/**
 * Handles recordings of an Experiment.
 *
 * FEATURES:
 *
 * - Record timings
 *
 * RELATIONS:
 *
 * - Recording is called by Experiment: every time a Benchmark round is executed, the Experiment
 *   records the timings through this class.
 */
public class ExperimentRecording {

    private List<Long> times, servicePercentiles, processingPercentiles, estimatedServicePercentiles;

    /**
     * Initialize this recording.
     */
    public ExperimentRecording() {
        times = new ArrayList<>();
        servicePercentiles = new ArrayList<>();
        processingPercentiles = new ArrayList<>();
        estimatedServicePercentiles = new ArrayList<>();
    }

    /**
     * Record the given times: the arrival of the request and the start/finish time of its
     * processing.
     */
    public void add(long timeNs, long servicePercentileNs, long processingPercentileNs,
                    long estimatedServicePercentileNs) {
        times.add(timeNs);
        servicePercentiles.add(servicePercentileNs);
        processingPercentiles.add(processingPercentileNs);
        estimatedServicePercentiles.add(estimatedServicePercentileNs);
    }

    public void printOut() {
        // header
        Sys.printlnOut("99% percentiles in ms");
        Sys.printlnOut(String.format("%14s;%14s;%14s;%14s",
                "load", "service", "processing", "est. service"));

        // body
        int size = servicePercentiles.size();
        int MILLION = 1_000_000;
        for (int i = 0; i<size; i++) {
            Sys.printlnOut(String.format("%14f;%14f;%14f;%14f",
                    (double) times.get(i) / MILLION,
                    (double) servicePercentiles.get(i) / MILLION,
                    (double) processingPercentiles.get(i) / MILLION,
                    (double) estimatedServicePercentiles.get(i) / MILLION));
        }
    }


}
