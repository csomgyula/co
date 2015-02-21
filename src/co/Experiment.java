package co;

import co.stat.Indicators;

import java.util.concurrent.TimeUnit;

/**
 * Represents a Load experiment, when the same Task is benchmarked under different Loads.
 *
 * TODO: each round should be executed in a separate process
 */
public abstract class Experiment {
    private long minTime, maxTime, deltaTime;
    private TimeUnit timeUnit;

    private int requestCount, warmupCount;

    /**
     * Returns the Task to be benchmarked.
     */
    public abstract Task getTask();

    /**
     * Returns the Load distribution.
     */
    public abstract Load getLoad(long time, TimeUnit unit);

    public long getMinTime() {
        return minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(long deltaTime) {
        this.deltaTime = deltaTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public int getWarmupCount() {
        return warmupCount;
    }

    public void setWarmupCount(int warmupCount) {
        this.warmupCount = warmupCount;
    }

    /**
     * Runs the experiment.
     */
    public void run() {
        // init load related stuff
        Load load;
        long time = getMinTime();
        long deltaTime = getDeltaTime();
        long maxTime = getMaxTime();
        TimeUnit timeUnit = getTimeUnit();

        // get task
        Task task = getTask();

        // init benchmark counts
        int requestCount = getRequestCount(), warmupCount = getWarmupCount();

        // init stat
        Stat stat = new Stat();
        Indicators indicators;
        ExperimentRecording recording = new ExperimentRecording();
        boolean exportRawStat = false;

        // main loop
        while (time < maxTime) {
            // run the benchmark under the given load
            indicators = new Benchmark(
                    getLoad(time, timeUnit), task, requestCount, warmupCount, exportRawStat)
                    .run(stat);

            // record the current benchmark stats
            recording.add(
                    TimeUnit.NANOSECONDS.convert(time, timeUnit),
                    indicators.getService().getPercentile(),
                    indicators.getProcessing().getPercentile(),
                    indicators.getEstimatedService().getPercentile());

            // decrease the load by increasing the time between consecutive requests
            time += deltaTime;

            // run gc
            System.gc();
        }

        // stats

        // TODO handle Stat
        recording.printOut();
    }
}
