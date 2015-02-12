package co.stat;

import co.Stat;
import co.Sys;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles raw statistics.
 *
 * FEATURES:
 *
 * - Records timings (recorded by the processing thread(s))
 * - Calculates various timings: idle, wait, dequeue, processing, grossProcessing, service time
 * - Writes the data out to a CSV file, for further analysis
 */
public class Raw implements Stat {
    // statistics recorded by the benchmark
    protected List<Long> arrivalList, startList, finishList;

    // statistics calculated by calculateTimings
    protected List<Long> idleList, waitList, dequeueList, processingList, grossProcessingList,
            serviceList;

    /**
     * Initialize the stat.
     */
    public Raw(){
        arrivalList = new ArrayList<>();
        startList = new ArrayList<>();
        finishList = new ArrayList<>();
    }

    /**
     * Record the given times: the arrival of the request and the start/finish time of its
     * processing.
     */
    @Override
    public void record(long arrivalNs, long startNs, long finishNs) {
        arrivalList.add(arrivalNs);
        startList.add(startNs);
        finishList.add(finishNs);
    }

    /**
     * Calculate statistics.
     */
    @Override
    public void process() throws IOException {
        calculateTimings();
        toCSV("raw_stat.csv");
        System.out.println("Raw stat: stats written to raw_stat.csv");
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
        idleList = new ArrayList<>();
        waitList = new ArrayList<>();
        dequeueList = new ArrayList<>();
        processingList = new ArrayList<>();
        grossProcessingList = new ArrayList<>();
        serviceList = new ArrayList<>();

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
     * Writes statistics to the given CSV file.
     */
    protected void toCSV(String path) throws IOException {
        File csvFile = new File(path);

        try (
                FileWriter fileWriter = new FileWriter(csvFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
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

    @Override
    public String toString() {
        return "Raw statistics";
    }
}