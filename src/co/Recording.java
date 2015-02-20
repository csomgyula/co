package co;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that handles recordings.
 *
 * FEATURES:
 *
 * - Records timings (called by Benchmark)
 */
public class Recording {
    /**
     * Payload is used to avoid dead code elimination. See the description of the Task object for
     * more.
     */
    public Object payload;

    // statistics recorded by the benchmark
    private List<Long> arrivals, starts, finishList;

    /**
     * Initialize the stat.
     */
    public Recording(){
        arrivals = new ArrayList<>();
        starts = new ArrayList<>();
        finishList = new ArrayList<>();
    }

    /**
     * Record the given times: the arrival of the request and the start/finish time of its
     * processing.
     *
     * Payload is used to avoid dead code elimination. See the description of the Task object for
     * more.
     */
    public void add(long arrivalNs, long startNs, long finishNs, Object payload) {
        arrivals.add(arrivalNs);
        starts.add(startNs);
        finishList.add(finishNs);
        this.payload = payload;
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