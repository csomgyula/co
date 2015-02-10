package co;

import java.util.*;
import java.io.*;

/**
 * Implements the necessary statistical logic.
 *
 * FEATURES:
 *
 * - Records timings (recorded by the processing thread(s))
 * - Able to write the data out to a CSV file, for further analysis
 *
 * TODO:
 *
 * - split the class into an interface and an impl
 */ 
public class Stat {
    // statistics recorded by the benchmark
    private List<Long> arrivalList, startList, finishList;
    
    // statistics calculated by calculateTimings
    private List<Long> idleList, waitList, dequeueList, processingList, grossProcessingList,
            serviceList;

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
                    "%25s: min: %f ms, avg: %f ms, %d%%: %f max: %f ms",
                    name,
                    min / MILLION,
                    average / MILLION,
                    percentage,
                    percentile / MILLION,
                    max / MILLION);
        }
    }

    /**
     * Initialize the stat.
     */
    public Stat(){
        arrivalList = new ArrayList<Long>();
        startList = new ArrayList<Long>();
        finishList = new ArrayList<Long>();
    }
    
    /**
     * Record the given times: the arrival of the request and the start/finish time of its 
     * processing.
     */
    public void record(long arrivalNs, long startNs, long finishNs) {
        arrivalList.add(arrivalNs);
        startList.add(startNs);
        finishList.add(finishNs);
    }
    
    /**
     * Calculate statistics.
     */
    public void calculate() throws IOException{
        calculateTimings();
        calculateIndicators();
        toCSV("stat.csv");
    }
    
    /**
     * Calculate the following times: idle, wait, dequeue, (gross)processing, service, where:
     *
     * idle time is the period during the request is not processed, formally:
     *   
     *      idle = start - arrival
     * 
     * dequeue is the time necessary to dequeue the request and start its processing, formally:
     *
     *                start - previous finish time : if the request is already in the queue when 
     *                                               the previous request finished 
     *      dequeue = 
     *                start - arrival time : if the request arrived later then the finish of the 
     *                                       previous request
     *
     * wait is the time while the request is in the queue:
     *
     *      wait = idle - dequeue
     *
     * processing is the active processing time of the request:
     *
     *      processing = finish - start
     *
     * grossProcessing is the active processing time of the request plus dequeue time:
     *
     *      grossProcessing = processing + dequeue
     *
     * service is the total service time starting from arrival up to finish:
     *
     *      service = finish - arrival
     */
    protected void calculateTimings() {
        idleList = new ArrayList<Long>();
        waitList = new ArrayList<Long>();
        dequeueList = new ArrayList<Long>();
        processingList = new ArrayList<Long>();
        grossProcessingList = new ArrayList<Long>();
        serviceList = new ArrayList<Long>();
        
        long arrival, start, finish = Long.MIN_VALUE, prevFinish, idle, wait, dequeue, processing, 
            processing2, total;
        
        int statCount = arrivalList.size();
        for (int i = 0; i < statCount; i++) {
            // get recorded times
            arrival = arrivalList.get(i);
            start = startList.get(i);
            prevFinish = finish;
            finish = finishList.get(i);
                
            // calculate times
            idle = start - arrival;
            if (i == 0) { dequeue = start - arrival; }
            else { dequeue = start - (prevFinish > arrival ? prevFinish : arrival); }
            wait = idle - dequeue;
            processing = finish - start;
            processing2 = processing + dequeue;
            total = idle + processing;
            
            // store new stats
            idleList.add(idle);
            waitList.add(wait);
            dequeueList.add(dequeue);
            processingList.add(processing);
            grossProcessingList.add(processing2);
            serviceList.add(total);
        }
    }
        
    /**
     * Calculate indicators for the following times: wait, dequeue, processing, grossProcessing.
     * See calculateTimings() for details.
     */
    protected void calculateIndicators() {
        idle = calculateIndicatorOf("idle time", idleList);
        wait = calculateIndicatorOf("wait time", waitList);
        dequeue = calculateIndicatorOf("dequeue time", dequeueList);
        processing = calculateIndicatorOf("processing time", processingList);
        grossProcessing = calculateIndicatorOf("gross processing time", grossProcessingList);
        service = calculateIndicatorOf("service time", serviceList);

        int MILLION = 1_000_000;
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
        List<Long> sampleClone = new ArrayList<Long>(sample);
        Collections.sort(sampleClone);
        int size = sampleClone.size();

        indicator.min = sampleClone.get(0);
        indicator.max = sampleClone.get(size - 1);

        indicator.percentage = 99;
        int percentageIndex = (int) ((long) indicator.percentage * (long) size / 100l);
        indicator.percentile = sampleClone.get(percentageIndex - 1);

        return indicator;
    }

    /**
     * Writes statistics to the given CSV file.
     */
    protected void toCSV(String path) throws IOException {
        File csvFile = new File(path);
        
        try (
            FileWriter fileWriter = new FileWriter(csvFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        ) {
            bufferedWriter.write(
                "arrival;start;finish;idle;wait;dequeue;processing;grossProcessing;service");
            bufferedWriter.newLine();
            int statCount = arrivalList.size();
            long arrival, start, finish, idle, wait, dequeue, processing, processing2, total;
            for (int i = 0; i < statCount; i++) {
                arrival = arrivalList.get(i) - Sys.TIME_ZERO;
                start = startList.get(i) - Sys.TIME_ZERO;
                finish = finishList.get(i) - Sys.TIME_ZERO;
                idle = idleList.get(i);
                dequeue = dequeueList.get(i);
                wait = waitList.get(i);
                processing = processingList.get(i);
                processing2 = grossProcessingList.get(i);
                total = serviceList.get(i);
                
                String timingsString = 
                    String.format("%d;%d;%d;%d;%d;%d;%d;%d;%d", arrival, start, finish, idle, wait,
                        dequeue, processing, processing2, total);
                bufferedWriter.write(timingsString);
                bufferedWriter.newLine();
            }
        }
    }
}