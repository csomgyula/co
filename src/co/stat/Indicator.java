package co.stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
