package co.stat;

import java.util.*;
import java.io.*;

/**
 * Handles indicators average, min, max, percentile time of idle, wait, dequeue, processing,
 * grossProcessing, service.
 *
 * FEATURES:
 *
 * - Records timings (inherited from Raw stat)
 * - Calculate indicators
 * - Print out indicators (to console)
 *
 * TODO:
 *
 * - May replace protected field access of Raw with getters
 */ 
public class Indicators extends Raw{
    // indicators calculated by calculateIndicator
    private Indicator idle, wait, dequeue, processing, grossProcessing, service;

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
    public void process() throws IOException{
        calculateTimings();
        calculateIndicators();
        printOutIndicators();
    }
        
    /**
     * Calculate indicators for the following times: wait, dequeue, processing, grossProcessing.
     * See Raw.calculateTimings() for more details.
     */
    protected void calculateIndicators() {
        idle = calculateIndicatorOf("idle time", idleList);
        wait = calculateIndicatorOf("wait time", waitList);
        dequeue = calculateIndicatorOf("dequeue time", dequeueList);
        processing = calculateIndicatorOf("processing time", processingList);
        grossProcessing = calculateIndicatorOf("gross processing time", grossProcessingList);
        service = calculateIndicatorOf("service time", serviceList);
    }

    protected void printOutIndicators() {
        System.out.println("Indicators:");
        System.out.println("  " + idle);
        System.out.println("  " + wait);
        System.out.println("  " + dequeue);
        System.out.println("  " + processing);
        System.out.println("  " + grossProcessing);
        System.out.println("  " + service);
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
}