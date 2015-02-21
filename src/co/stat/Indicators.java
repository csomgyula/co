package co.stat;

import co.Sys;

import java.util.*;

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
public class Indicators{
    private Raw raw;

    // indicators calculated by calculateIndicator
    private Indicator idle, wait, dequeue, processing, grossProcessing, service, arrivalDiff,
        estimatedService;

    /**
     * Struct that holds the following indicators of a sample: average value, min/max and Nth
     * percentile.
     */
    public class Indicator {
        private String name;
        private double average, min, max;
        private long percentile;
        private int percentage;

        public Indicator(String name) {
            this.name = name;
        }

        public Indicator(String name, List<Long> sample) {
            this.name = name;

            // average
            average = sample.stream().mapToLong((val) -> val).average().getAsDouble();

            // min, max, percentile
            List<Long> sampleClone = new ArrayList<>(sample);
            Collections.sort(sampleClone);
            int size = sampleClone.size();

            min = sampleClone.get(0);
            max = sampleClone.get(size - 1);

            percentage = 99;
            int percentageIndex = (int) ((long) percentage * (long) size / 100l);
            percentile = sampleClone.get(percentageIndex - 1);
        }

        public String getName() {
            return name;
        }

        public double getAverage() {
            return average;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public long getPercentile() {
            return percentile;
        }

        public int getPercentage() {
            return percentage;
        }

        @Override
        public String toString() {
            int MILLION = 1_000_000;
            return String.format(
                    "%25s: min: %f ms, avg: %f ms, %d%%: %f ms, max: %f ms",
                    name,
                    min / MILLION,
                    average / MILLION,
                    percentage,
                    ((double) percentile) / MILLION,
                    max / MILLION);
        }
    }

    public Indicators(Raw raw) {
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
        Sys.printOut("Indicators:");
        Sys.printOut("  " + service);
        Sys.printOut("  " + estimatedService);
        Sys.printOut("  " + processing);
        Sys.printOut(sep);
        Sys.printOut("  " + arrivalDiff);
        Sys.printOut("  " + grossProcessing);
        Sys.printOut("  " + processing);
        Sys.printOut("  " + dequeue);
        Sys.printOut(sep);
        Sys.printOut("  " + idle);
        Sys.printOut("  " + wait);
        Sys.printOut("  " + dequeue);
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