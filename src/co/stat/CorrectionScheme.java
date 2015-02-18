package co.stat;

import co.Sys;

import java.util.ArrayList;
import java.util.List;

/**
 * The correction scheme proposed in the paper.
 *
 * See: <https://github.com/csomgyula/co/blob/master/paper.md>
 *
 */
public class CorrectionScheme {

    /**
     * Implements the correction scheme proposed in the paper.
     */
     public List<Long> calculatedServiceTimes(List<Long> arrivalTimes, List<Long> processingTimes) {
         int size = arrivalTimes.size(), checkSize = processingTimes.size();
         Sys.assertTrue(size == checkSize);

         List<Long> startTimes = startTimes(arrivalTimes, processingTimes);

         List<Long> calculatedServiceTimes = new ArrayList<>();
         long calculatedServiceTime;
         for (int i = 1; i < size; i++) {
             calculatedServiceTime = startTimes.get(i) - arrivalTimes.get(i)
                     + processingTimes.get(i);
             calculatedServiceTimes.add(calculatedServiceTime);
         }
         return calculatedServiceTimes;
    }

    /**
     * Implements the recursive algorithm for start times proposed in the paper.
     */
    protected List<Long> startTimes(List<Long> arrivalTimes, List<Long> processingTimes) {
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
            startTimes.add(startTime);
        }

        return startTimes;
    }
}
