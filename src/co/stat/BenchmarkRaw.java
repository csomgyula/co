package co.stat;

import co.BenchmarkRecording;
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
 * - Calculates various timings: idle, wait, dequeue, processing, grossProcessing, service, arrival
 *   diff time and calculated service time as per the correction scheme proposed in the paper
 * - Writes the data out to a CSV file, for further analysis
 *
 * Each method is called by Stat
 */
public class BenchmarkRaw {
    private BenchmarkRecording recording;

    // statistics calculated by calculateTimings
    private List<Long> idles, waits, dequeues, processings, grossProcessings,
            services, arrivalDiffs, estimatedServices;

    public BenchmarkRaw(BenchmarkRecording recording) {
        this.recording = recording;
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
    public void calculate() {
        List<Long> arrivalList = recording.getArrivals(), startList = recording.getStarts(),
                finishList = recording.getFinishList();
        idles = new ArrayList<>();
        waits = new ArrayList<>();
        dequeues = new ArrayList<>();
        processings = new ArrayList<>();
        grossProcessings = new ArrayList<>();
        services = new ArrayList<>();
        arrivalDiffs = new ArrayList<>();

        long arrival, prevArrival = 0, arrivalDiff = 0, start, finish = Long.MIN_VALUE, prevFinish,
                idle, wait, dequeue, processing, grossProcessing, total;

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
            grossProcessing = processing + dequeue;
            total = idle + processing;

            // store new stats
            idles.add(idle);
            waits.add(wait);
            dequeues.add(dequeue);
            processings.add(processing);
            grossProcessings.add(grossProcessing);
            services.add(total);
            if (i > 0)
                arrivalDiffs.add(arrivalDiff);
        }

        calculateEstimatedServices();
    }

    /**
     * Calculated service times from arrival- and processing times by the correction scheme
     * proposed in the paper. See:
     *
     * - CorrectionScheme
     * - <https://github.com/csomgyula/co/blob/master/paper.md>
     */
    protected void calculateEstimatedServices() {
        estimatedServices = new CorrectionScheme().estimateServiceTimes(
                recording.getArrivals(), getProcessings());
    }

    /**
     * Writes statistics to the given CSV file.
     */
    public void toCSV(String path) {
        File csvFile = new File(path);
        List<Long> arrivalList = recording.getArrivals(), startList = recording.getStarts(),
                finishList = recording.getFinishList();

        try (
                FileWriter fileWriter = new FileWriter(csvFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ) {
            bufferedWriter.write(
                    "arrival;start;finish;idle;wait;dequeue;processing;grossProcessing;service");
            bufferedWriter.newLine();
            int statCount = arrivalList.size();
            long arrival, start, finish, idle, wait, dequeue, processing, grossProcessing, total;
            for (int i = 0; i < statCount; i++) {
                arrival = arrivalList.get(i) - Sys.TIME_ZERO;
                start = startList.get(i) - Sys.TIME_ZERO;
                finish = finishList.get(i) - Sys.TIME_ZERO;
                idle = idles.get(i);
                dequeue = dequeues.get(i);
                wait = waits.get(i);
                processing = processings.get(i);
                grossProcessing = grossProcessings.get(i);
                total = services.get(i);

                String timingsString =
                        String.format("%d;%d;%d;%d;%d;%d;%d;%d;%d", arrival, start, finish, idle, wait,
                                dequeue, processing, grossProcessing, total);
                bufferedWriter.write(timingsString);
                bufferedWriter.newLine();
            }
        }
        catch (IOException e) {
            Sys.debug(e.getMessage());
        }

        Sys.printOut("Raw stat written to " + path);
    }

    public BenchmarkRecording getRecording() {
        return recording;
    }

    public List<Long> getIdles() {
        return idles;
    }

    public List<Long> getWaits() {
        return waits;
    }

    public List<Long> getDequeues() {
        return dequeues;
    }

    public List<Long> getProcessings() {
        return processings;
    }

    public List<Long> getGrossProcessings() {
        return grossProcessings;
    }

    public List<Long> getServices() {
        return services;
    }

    public List<Long> getArrivalDiffs() {
        return arrivalDiffs;
    }

    public List<Long> getEstimatedServices() {
        return estimatedServices;
    }
}