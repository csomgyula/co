package co.stat;

import co.Sys;

import java.util.ArrayList;
import java.util.List;

/**
 * The correction scheme proposed in the paper.
 *
 * See: <https://github.com/csomgyula/co/blob/master/paper.md>
 *
 * FEATURES:
 *
 * - Calculates estimated service times by the algorithm proposed in the paper (called by Stat)
 */
public class CorrectionScheme {

    /**
     * Implements the correction scheme proposed in the paper. Estimates service times from arrival,
     * and processing times.
     */
     public List<Long> estimateServiceTimes(List<Long> arrivalTimes, List<Long> processingTimes) {
         int size = arrivalTimes.size();
         Sys.assertTrue(size == processingTimes.size());

         List<Long> startTimes = startTimes(arrivalTimes, null, 0, processingTimes);

         return serviceTimes(arrivalTimes, startTimes, processingTimes);
    }

    /**
     * Implements the correction scheme when an upper bound is known for the dequeue times.
     */
    public List<Long> estimateServiceTimes(List<Long> arrivalTimes, long maxDequeueTime,
                                           List<Long> processingTimes) {
        int size = arrivalTimes.size();
        Sys.assertTrue(size == processingTimes.size());

        List<Long> startTimes = startTimes(arrivalTimes, null, maxDequeueTime,
                processingTimes);

        return serviceTimes(arrivalTimes, startTimes, processingTimes);
    }

    /**
     * Implements a correction scheme when besides arrivals and processing times, dequeue times are
     * known as well.
     */
    public List<Long> calculateServiceTimes(List<Long> arrivalTimes, List<Long> dequeueTimes,
                                            List<Long> processingTimes) {
        int size = arrivalTimes.size();
        Sys.assertTrue(size == dequeueTimes.size() && size == processingTimes.size());

        List<Long> startTimes = startTimes(arrivalTimes, dequeueTimes, 0, processingTimes);

        return serviceTimes(arrivalTimes, startTimes, processingTimes);
    }

    /**
     * Calculates the service time from arrivals, starts and processing times.
     */
    protected List<Long> serviceTimes(List<Long> arrivalTimes, List<Long> startTimes,
                                   List<Long> processingTimes) {
        int size = arrivalTimes.size();
        List<Long> serviceTimes = new ArrayList<>();
        long serviceTime;
        for (int i = 1; i < size; i++) {
            serviceTime = startTimes.get(i) - arrivalTimes.get(i)
                    + processingTimes.get(i);
            serviceTimes.add(serviceTime);
        }
        return serviceTimes;
    }

    /**
     * Implements the recursive algorithm to calculate the start times. Handles three cases:
     *
     * - neither dequeue times nor an upper bound is known
     * - an upper bound for dequeue times is known
     * - dequeue times are known
     */
    protected List<Long> startTimes(List<Long> arrivalTimes, List<Long> dequeueTimes,
                                    long maxDequeueTime,
                                    List<Long> processingTimes) {
        int size = arrivalTimes.size();

        List<Long> startTimes = new ArrayList<>();

        long arrivalTime = arrivalTimes.get(0);
        long startTime = arrivalTime;
        long prevFinishTime;
        startTimes.add( startTime );

        for (int i = 1; i < size; i++) {
            arrivalTime = arrivalTimes.get(i);
            prevFinishTime = startTime + processingTimes.get(i - 1);
            startTime = prevFinishTime > arrivalTime ? prevFinishTime : arrivalTime;
            if (dequeueTimes != null) {
                startTime += dequeueTimes.get(i);
            }
            else {
                startTime += maxDequeueTime;
            }
            startTimes.add(startTime);
        }

        return startTimes;
    }
}
