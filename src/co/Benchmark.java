package co;

import co.load.Exponential;
import co.stat.Indicators;
import co.task.Fibonacci;

import java.util.concurrent.TimeUnit;

/**
 * Represents the benchmark runner that implements the schedule-run-measure model. It executes 
 * the given task as many times as the given request count within the following loop:
 * 
 * 1. schedule the execution according to the given load distribution
 * 2. execute the given task
 * 3. measure the execution time and record it
 * 
 * FEATURES:
 *
 *  - Warms up the environment 
 *  - Schedule-run-measure
 *  - Configurable 
 *    - warm up count
 *    - request count
 *    - task represented by the Task interface
 *    - load distribution represented by the Load interface
 *  
 * DESIGN: 
 *
 *  ![](http://yuml.me/d856bbd5)
 *
 * TODO: Rethink warm up logic
 */
public class Benchmark {
    private final Load load;
    private final Task task;
    private final int requestCount;
    private final Stat stat;
    private final int warmupCount;
    
    /** 
     * Initializes the benchmark with the given arguments.
     */
    public Benchmark(Load load, Task task, int requestCount, int warmupCount, Stat stat) {
        Sys.assertTrue(load != null && task != null && requestCount >= 0 && stat != null && 
            warmupCount >= 0);
        
        this.load = load;
        this.task = task;
        this.requestCount = requestCount;
        this.stat = stat;
        this.warmupCount = warmupCount;
    }
    
    /**
     * The main benchmarking method.
     */
    public long run() {
        Sys.debug("benchmark started");
        
        long startedNs, finishedNs = 0, arrivalNs, benchmarkStartedNs = 0l;
        int processedCount = 0;
        
        arrivalNs = System.nanoTime();

        while(processedCount < requestCount + warmupCount) {
            // if (Sys.DEBUG) { Sys.debug("processing: " + (processedCount - warmupCount)); }
            
            // after warm up reset arrival time to now
            // this has the effect as if we flushed (empty) the request queue since from now on 
            // each requests' arrival time will  be in the future
            if (processedCount == warmupCount) {
                benchmarkStartedNs = System.nanoTime();
                arrivalNs = System.nanoTime();
            }
            
            // schedule
            arrivalNs += load.nextRelativeTimeNs();
            if (finishedNs < arrivalNs) { sleepUntilNextRequest(arrivalNs); }
            
            // run & measure FIXME: can the compiler reorder these actions?
            startedNs = System.nanoTime();
            task.execute();
            finishedNs = System.nanoTime();
            
            // increment processed count
            processedCount++;
            
            // the first runs are warm up ones, only record stats if warm up is over
            if (processedCount > warmupCount) { stat.record(arrivalNs, startedNs, finishedNs); }
        }

        Sys.debug("benchmark finished");

        return System.nanoTime() - benchmarkStartedNs;
    }
    
    /**
     * Sleeps until the given arrival time. The time must be in line with System.nanoTime().
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
        // init benchmark
        Sys.timeZero();
        Task task = new Fibonacci(1_000_000);
        int requestCount = 1000;
        int warmupCount = 1000;
        // Load load = new co.load.Steady(1900, TimeUnit.MICROSECONDS);
        Load load = new Exponential(30000, TimeUnit.MICROSECONDS);
        Stat stat = new Indicators();
        // Stat stat = new Raw();
        Benchmark benchmark = new Benchmark(load, task, requestCount, warmupCount, stat);

        System.out.println("Load: " + load);
        System.out.println("Task: " + task);
        System.out.println("Request count: " + requestCount);
        System.out.println("Stat: " + stat);

        // run benchmark
        System.out.print("\nBenchmarking... ");
        long runTime = benchmark.run();
        System.out.println("done in " + runTime / 1_000_000 + " ms\n");

        // process stats
        stat.process();
    }   
}