package co.stat;

import java.util.*;
import java.io.*;

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
 * - Records timings (inherited from Raw stat)
 * - Calculate indicators
 * - Print out indicators (to console)
 */ 
public class Indicators extends Raw{
    // indicators calculated by calculateIndicator
    private Indicator idle, wait, dequeue, processing, grossProcessing, service, arrivalDiff,
        calculatedService;

    /**
     * Struct that holds the following indicators of a sample: average value, min/max and Nth
     * percentile.
     */
    protected class Indicator {
        protected String name;
        protected double average, min, max, percentile;
        protected int percentage;

        public Indicator(String name) {
            this.name = name;
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
                    percentile / MILLION,
                    max / MILLION);
        }
    }

    /**
     * Calculate statistics.
     */
    @Override
    public void postProcess() throws IOException{
        calculateTimings();
        calculateIndicators();
        printOutIndicators();
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
    protected void calculateIndicators() {
        idle = calculateIndicatorOf("idle time", getIdleList());
        wait = calculateIndicatorOf("wait time", getWaitList());
        dequeue = calculateIndicatorOf("dequeue time", getDequeueList());
        processing = calculateIndicatorOf("processing time", getProcessingList());
        grossProcessing = calculateIndicatorOf("gross processing time", getGrossProcessingList());
        service = calculateIndicatorOf("service time", getServiceList());
        arrivalDiff = calculateIndicatorOf("arrival diff", getArrivalDiffList());
        calculatedService = calculateIndicatorOf("calculated service time", getCalculatedServiceList());
    }

    protected void printOutIndicators() {
        String sep = "---------------------------------------------------------------------" +
                "------------------------------------------------";
        System.out.println("Indicators:");
        System.out.println("  " + service);
        System.out.println("  " + calculatedService);
        System.out.println("  " + processing);
        System.out.println(sep);
        System.out.println("  " + arrivalDiff);
        System.out.println("  " + grossProcessing);
        System.out.println("  " + processing);
        System.out.println("  " + dequeue);
        System.out.println(sep);
        System.out.println("  " + idle);
        System.out.println("  " + wait);
        System.out.println("  " + dequeue);
    }



    protected Indicator calculateIndicatorOf(String name, List<Long> sample) {
        Indicator indicator = new Indicator(name);

        // average
        indicator.average = sample.stream().mapToLong((val) -> val).average().getAsDouble();

        // min, max, percentile
        List<Long> sampleClone = new ArrayList<>(sample);
        Collections.sort(sampleClone);
        int size = sampleClone.size();

        indicator.min = sampleClone.get(0);
        indicator.max = sampleClone.get(size - 1);

        indicator.percentage = 99;
        int percentageIndex = (int) ((long) indicator.percentage * (long) size / 100l);
        indicator.percentile = sampleClone.get(percentageIndex - 1);

        return indicator;
    }

    @Override
    public String toString() {
        return "Indicator statistics";
    }
}