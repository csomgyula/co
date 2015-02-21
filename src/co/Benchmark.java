package co;

import java.util.concurrent.TimeUnit;

/**
 * Represents the benchmark runner that implements the schedule-run-measure model. It executes 
 * the given task as many times as the given request count within the following loop:
 * 
 * 1. schedule the execution according to the given load distribution
 * 2. execute the given task
 * 3. measure the execution time and record it
 *
 * Before entering the loop warms up. After executing the loop calculate statistics.
 *
 * FEATURES:
 *
 *  - Schedule-run-measure
 *  - Warms up the environment
 *  - Some support to prevent from dead code elimination (see Task interface for more)
 *  - Configurable Task, Load distribution, warm up- and request count
 */
public class Benchmark {
    private final Load load;
    private final Task task;
    private final int requestCount;
    private final int warmupCount;
    private final boolean exportRawStat;

    /** 
     * Initializes the benchmark with the given arguments.
     */
    public Benchmark(Load load, Task task, int requestCount, int warmupCount,
                     boolean exportRawStat) {
        Sys.assertTrue(load != null && task != null && requestCount >= 0 && warmupCount >= 0);
        
        this.load = load;
        this.task = task;
        this.requestCount = requestCount;
        this.warmupCount = warmupCount;
        this.exportRawStat = exportRawStat;
    }

    /**
     * Payload is used to avoid dead code elimination. See the description of the Task object for
     * more.
     */
    public Object payload;

    public void run() {
        // init benchmark
        Sys.timeZero();

        // configuration info
        Sys.printOut("Load: " + load);
        Sys.printOut("Task: " + task);
        Sys.printOut("Warmup count: " + requestCount);
        Sys.printOut("Request count: " + requestCount);

        Sys.printOut("\nBenchmarking... ");

        Recording recording;

        // warmup
        long warmupStarted = System.nanoTime();
        recording = new Recording();
        run("warmup", warmupCount, recording);

        // benchmark
        long benchmarkStarted = System.nanoTime();
        recording = new Recording();
        run("benchmark", requestCount, recording);
        long benchmarkFinished = System.nanoTime();

        // runtime info
        long runTime = benchmarkFinished - benchmarkStarted, fullRuntime = benchmarkFinished -
                warmupStarted;
        Sys.printOut("done in " + runTime / 1_000_000 + " (" + fullRuntime / 1_000_000
                + ") " + " ms\n");

        // stat
        Stat stat = new Stat();
        stat.process(recording, exportRawStat);
    }

    /**
     * The main benchmarking method.
     */
    protected long run(String name, int requestCount, Recording recording) {
        Sys.debug(name + " started");
        
        long startedNs, finishedNs = Long.MIN_VALUE, arrivalNs, benchmarkStartedNs = 0l;
        int processedCount = 0;

        arrivalNs = System.nanoTime();
        benchmarkStartedNs = System.nanoTime();
        while(processedCount < requestCount) {
            // if (Sys.DEBUG) { Sys.debug("processing: " + processedCount); }
            
            // schedule
            arrivalNs += load.nextRelativeTimeNs();
            if (finishedNs < arrivalNs) { sleepUntilNextRequest(arrivalNs); }
            
            // run & measure FIXME: can the compiler reorder these actions?
            startedNs = System.nanoTime();
            payload = task.execute();
            finishedNs = System.nanoTime();
            
            // increment processed count
            processedCount++;
            
            // record stats
            recording.add(arrivalNs, startedNs, finishedNs);
        }

        Sys.debug(name + " finished");

        return System.nanoTime() - benchmarkStartedNs;
    }
    
    /**
     * Sleeps until the given arrival time. The time must be in line with System.nanoTime().
     *
     * TODO: may move to Sys
     */
    protected void sleepUntilNextRequest(long arrivalNs) {
        long nowNs;
        int sleepTimeMs, sleepTimeNs;
        while ((nowNs = System.nanoTime()) < arrivalNs) {
            sleepTimeMs = (int) TimeUnit.MILLISECONDS.convert(arrivalNs 
                - nowNs, TimeUnit.NANOSECONDS);
            sleepTimeNs = (int) (arrivalNs - nowNs - 
                TimeUnit.NANOSECONDS.convert(sleepTimeMs, TimeUnit.MILLISECONDS));
            try { Thread.sleep(sleepTimeMs, sleepTimeNs); }
            catch(InterruptedException e) { Sys.debug("scheduled wait interrupted"); }
        }
    }
    
    
    public static void main(String[] args) throws Exception{
        Load load = new co.load.Steady(4100, TimeUnit.MICROSECONDS);
        // Load load = new co.load.Exponential(900, TimeUnit.MICROSECONDS);

        Task task = new co.task.Fibonacci(5_000_000);
        // Task task = new co.task.Counter();

        int requestCount = 1000;
        int warmupCount = 1000;

        boolean exportRawStat = true, info = true;

        // Sys.PRINTOUT = false;
        Benchmark benchmark = new Benchmark(load, task, requestCount, warmupCount, exportRawStat);

        // run benchmark
        benchmark.run();
    }   
}