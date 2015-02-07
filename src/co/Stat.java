package co;

import co.stat.Average;

import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Implements the necessary statistical logic.
 *
 * FEATURES:
 *
 * - Records timings (recorded by the processing thread(s))
 * - Able to write the data out to a CSV file, for further analysis
 */ 
public class Stat {
    // statistics recorded by the benchmark
    private List<Long> arrivalList, startList, finishList;
    
    // statistics calculated by this object
    private List<Long> idleList, waitList, dequeueList, processingList, processing2List, totalList;
    private double avgIdle, avgWait, avgDequeue, avgProcessing, avgProcessing2, avgTotal;
    
    /**
     * Initialize the stat.
     */
    protected Stat(){ 
        arrivalList = new ArrayList<Long>();
        startList = new ArrayList<Long>();
        finishList = new ArrayList<Long>();
    }
    
    /**
     * Record the given times: the arrival of the request and the start/finish time of its processing.
     */
    public void record(long arrivalNs, long startNs, long finishNs) {
        arrivalList.add(arrivalNs);
        startList.add(startNs);
        finishList.add(finishNs);
    }
    
    /**
     * Calculate statistics.
     */
    public void process() throws IOException{
        calculateTimings();
        averages();
        toCSV("stat.csv");
    }
    
    /**
     * Calculate the following times: idle, wait, dequeue, processing, total, where:
     *
     * idle time is the period during the request is not processed, formally:
     *   
     *      idle = start - arrival
     * 
     * dequeue is the time necessary to dequeue the request and start its processing, formally:
     *
     *                start - previous finish time : if the request is already in the queue when the previous request finished 
     *      dequeue = 
     *                start - arrival time : if the request arrived later then the finish of the previous request
     *
     * wait is the time while the request is in the queue:
     *
     *      wait = idle - dequeue
     *
     * processing is the active processing time of the request:
     *
     *      processing = finish - start
     *
     * processing2 is the active processing time of the request plus dequeue time:
     *
     *      processing2 = processing + dequeue
     *
     * total is the total time from arrival to finish:
     *
     *      total = finish - start
     */
    protected void calculateTimings() {
        idleList = new ArrayList<Long>();
        waitList = new ArrayList<Long>();
        dequeueList = new ArrayList<Long>();
        processingList = new ArrayList<Long>();
        processing2List = new ArrayList<Long>();
        totalList = new ArrayList<Long>();
        
        long arrival, start, finish = Long.MIN_VALUE, prevFinish, idle, wait, dequeue, processing, processing2, total;
        
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
            processing2List.add(processing2);
            totalList.add(total);
        }
    }
        
    /**
     * Calculate averages for the following times: wait, dequeue, processing, processing2. See calculateTimings() for details.
     */
    protected void averages() {
        Average average = new Average(); 
        int MILLION = 1_000_000;
        
        avgIdle = idleList.stream().mapToLong((val) -> val).average().getAsDouble();
        avgWait = waitList.stream().mapToLong((val) -> val).average().getAsDouble();
        avgDequeue = dequeueList.stream().mapToLong((val) -> val).average().getAsDouble();
        avgProcessing = processingList.stream().mapToLong((val) -> val).average().getAsDouble();
        avgProcessing2 = processing2List.stream().mapToLong((val) -> val).average().getAsDouble();
        avgTotal = totalList.stream().mapToLong((val) -> val).average().getAsDouble();
        
        System.out.println("Averages:");
        System.out.println("  idle time:             " + avgIdle / MILLION + " ms");
        System.out.println("  wait time:             " + avgWait / MILLION + " ms");
        System.out.println("  dequeue time:          " + avgDequeue / MILLION + " ms");
        System.out.println("  processing time:       " + avgProcessing / MILLION + " ms");
        System.out.println("  gross processing time: " + avgProcessing2 / MILLION + " ms (processing time + dequeue time)");
        System.out.println("  total time:            " + avgTotal / MILLION + " ms");
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
            bufferedWriter.write("arrival;start;finish;idle;wait;dequeue;processing;processing2;total");
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
                processing2 = processing2List.get(i);
                total = totalList.get(i);
                
                String timingsString = 
                    String.format("%d;%d;%d;%d;%d;%d;%d;%d;%d", arrival, start, finish, idle, wait, dequeue, processing, processing2, total);
                bufferedWriter.write(timingsString);
                bufferedWriter.newLine();
            }
        }
    }
}