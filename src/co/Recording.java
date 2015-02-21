package co;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that handles recordings.
 *
 * FEATURES:
 *
 * - Record timings
 *
 * RELATIONS:
 *
 * - Recording is called by Benchmark during the Benchmark: every time the Benchmark executed the
 *   benchmarked method, it records the timings through this class.
 */
public class Recording {

    // statistics recorded by the benchmark
    private List<Long> arrivals, starts, finishList;

    /**
     * Initialize this recording.
     */
    public Recording(){
        arrivals = new ArrayList<>();
        starts = new ArrayList<>();
        finishList = new ArrayList<>();
    }

    /**
     * Record the given times: the arrival of the request and the start/finish time of its
     * processing.
     */
    public void add(long arrivalNs, long startNs, long finishNs) {
        arrivals.add(arrivalNs);
        starts.add(startNs);
        finishList.add(finishNs);
    }

    public List<Long> getArrivals() {
        return arrivals;
    }

    public List<Long> getStarts() {
        return starts;
    }

    public List<Long> getFinishList() {
        return finishList;
    }
}