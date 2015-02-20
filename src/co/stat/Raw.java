package co.stat;

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
 * - Records timings (inherited from Recording)
 * - Calculates various timings: idle, wait, dequeue, processing, grossProcessing, service, arrival
 *   diff time and calculated service time as per the correction scheme proposed in the paper
 * - Writes the data out to a CSV file, for further analysis
 */
public class Raw extends Recording {
    // statistics calculated by calculateTimings
    private List<Long> idleList, waitList, dequeueList, processingList, grossProcessingList,
            serviceList, arrivalDiffList, calculatedServiceList;
    /**
     * Calculate statistics.
     */
    @Override
    public void postProcess() throws IOException {
        calculateTimings();
        toCSV("raw_stat.csv");
        System.out.println("Raw stat: stats written to raw_stat.csv");
    }

    /**
     * Calculate the following times: idle, wait, dequeue, (gross)processing, service, arrival diff,
     * where:
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
     *
     * arrival diff is the time diff between two consecutive requests, that is:
     *
     *      arrival diff = arrival - previous arrival
     */
    protected void calculateTimings() {
        List<Long> arrivalList = super.getArrivalList(), startList = super.getStartList(),
                finishList = super.getFinishList();
        idleList = new ArrayList<>();
        waitList = new ArrayList<>();
        dequeueList = new ArrayList<>();
        processingList = new ArrayList<>();
        grossProcessingList = new ArrayList<>();
        serviceList = new ArrayList<>();
        arrivalDiffList = new ArrayList<>();

        long arrival, prevArrival = 0, arrivalDiff = 0, start, finish = Long.MIN_VALUE, prevFinish,
                idle, wait, dequeue, processing, processing2, total;

        int statCount = arrivalList.size();
        for (int i = 0; i < statCount; i++) {
            // get recorded times
            arrival = arrivalList.get(i);
            start = startList.get(i);
            prevFinish = finish;
            finish = finishList.get(i);

            // calculate times
            idle = start - arrival;
            if (i == 0) {
                dequeue = start - arrival;
            }
            else {
                arrivalDiff = arrival - prevArrival;
                dequeue = start - (prevFinish > arrival ? prevFinish : arrival);
            }
            prevArrival = arrival;
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
            if (i > 0)
                arrivalDiffList.add(arrivalDiff);
        }

        calculateServiceTimesByTheCorrectionScheme();
    }

    /**
     * Calculated service times from arrival- and processing times by the correction scheme
     * proposed in the paper. See:
     *
     * - CorrectionScheme
     * - <https://github.com/csomgyula/co/blob/master/paper.md>
     */
    protected void calculateServiceTimesByTheCorrectionScheme() {
        calculatedServiceList = new CorrectionScheme().estimateServiceTimes(getArrivalList(),
                getProcessingList());
    }

    /**
     * Writes statistics to the given CSV file.
     */
    protected void toCSV(String path) throws IOException {
        File csvFile = new File(path);
        List<Long> arrivalList = super.getArrivalList(), startList = super.getStartList(),
                finishList = super.getFinishList();

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

    public List<Long> getIdleList() {
        return idleList;
    }

    public List<Long> getWaitList() {
        return waitList;
    }

    public List<Long> getDequeueList() {
        return dequeueList;
    }

    public List<Long> getProcessingList() {
        return processingList;
    }

    public List<Long> getGrossProcessingList() {
        return grossProcessingList;
    }

    public List<Long> getServiceList() {
        return serviceList;
    }

    public List<Long> getArrivalDiffList() {
        return arrivalDiffList;
    }

    public List<Long> getCalculatedServiceList() {
        return calculatedServiceList;
    }

    @Override
    public String toString() {
        return "Raw statistics";
    }
}