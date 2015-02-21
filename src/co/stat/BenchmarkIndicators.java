package co.stat;

import co.Sys;

/**
 * Handles indicators, ie. average/min/max/percentile time of idle, wait, dequeue, processing,
 * grossProcessing, service, arrival diff and also the calculated service time as per the paper. For
 * the latter see:
 *
 * - CorrectionScheme
 * - <https://github.com/csomgyula/co/blob/master/paper.md>
 *
 * FEATURES:
 *
 * - Calculate indicators
 * - Print out indicators (to console)
 *
 * Each method is called by Stat
 */ 
public class BenchmarkIndicators {
    private BenchmarkRaw raw;

    // indicators calculated by calculateIndicator
    private Indicator idle, wait, dequeue, processing, grossProcessing, service, arrivalDiff,
        estimatedService;

    public BenchmarkIndicators(BenchmarkRaw raw) {
        this.raw = raw;
    }
        
    /**
     * Calculate indicators for the following times:
     *
     * - wait
     * - dequeue
     * - processing
     * - grossProcessing
     * - service
     * - arrival diff
     * - service times calculated from arrivals and processing times
     *
     * See Raw.calculateTimings() for more details and especially for the latter see:
     *
     * - CorrectionScheme
     * - <https://github.com/csomgyula/co/blob/master/paper.md>
     */
    public void calculate() {
        idle = new Indicator("idle time", raw.getIdles());
        wait = new Indicator("wait time", raw.getWaits());
        dequeue = new Indicator("dequeue time", raw.getDequeues());
        processing = new Indicator("processing time", raw.getProcessings());
        grossProcessing = new Indicator("gross processing time", raw.getGrossProcessings());
        service = new Indicator("service time", raw.getServices());
        arrivalDiff = new Indicator("arrival diff", raw.getArrivalDiffs());
        estimatedService = new Indicator("calculated service time",
                raw.getEstimatedServices());
    }

    public void printOut() {
        String sep = "---------------------------------------------------------------------" +
                "------------------------------------------------";
        Sys.printlnOut("Indicators:");
        Sys.printlnOut("  " + service);
        Sys.printlnOut("  " + estimatedService);
        Sys.printlnOut("  " + processing);
        Sys.printlnOut(sep);
        Sys.printlnOut("  " + arrivalDiff);
        Sys.printlnOut("  " + grossProcessing);
        Sys.printlnOut(sep);
        Sys.printlnOut("  " + idle);
        Sys.printlnOut("  " + wait);
        Sys.printlnOut("  " + dequeue);
    }

    public Indicator getIdle() {
        return idle;
    }

    public Indicator getWait() {
        return wait;
    }

    public Indicator getDequeue() {
        return dequeue;
    }

    public Indicator getProcessing() {
        return processing;
    }

    public Indicator getGrossProcessing() {
        return grossProcessing;
    }

    public Indicator getService() {
        return service;
    }

    public Indicator getArrivalDiff() {
        return arrivalDiff;
    }

    public Indicator getEstimatedService() {
        return estimatedService;
    }
}