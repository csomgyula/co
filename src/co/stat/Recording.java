package co.stat;

import co.Stat;
import co.Sys;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class that handles recordings.
 *
 * FEATURES:
 *
 * - Records timings (recorded by the processing thread(s))
 */
public abstract class Recording implements Stat {
    /**
     * Payload is used to avoid dead code elimination. See the description of the Task object for
     * more.
     */
    public Object payload;

    // statistics recorded by the benchmark
    private List<Long> arrivalList, startList, finishList;

    /**
     * Initialize the stat.
     */
    public Recording(){
        arrivalList = new ArrayList<>();
        startList = new ArrayList<>();
        finishList = new ArrayList<>();
    }

    /**
     * Record the given times: the arrival of the request and the start/finish time of its
     * processing.
     *
     * Payload is used to avoid dead code elimination. See the description of the Task object for
     * more.
     */
    @Override
    public void record(long arrivalNs, long startNs, long finishNs, Object payload) {
        arrivalList.add(arrivalNs);
        startList.add(startNs);
        finishList.add(finishNs);
        this.payload = payload;
    }

    public List<Long> getArrivalList() {
        return arrivalList;
    }

    public List<Long> getStartList() {
        return startList;
    }

    public List<Long> getFinishList() {
        return finishList;
    }
}