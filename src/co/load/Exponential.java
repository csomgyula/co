package co.load;

import co.Load;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Represents the load of a Poisson process[1]. That is the time between consecutive requests follow
 * an exponential distribution[2].
 *
 * References:
 *
 * - [1] <http://en.wikipedia.org/wiki/Poisson_process>
 * - [2] <http://en.wikipedia.org/wiki/Exponential_distribution>
 */
public class Exponential implements Load {
    private final long meanTimeNs;
    private Random random;

    /**
     * Initializes the load generator with the given mean time and time unit.
     * The given mean time should represent the mean (average) time between two consecutive
     * requests.
     *
     * Speaking formally the load will produce samples from the following probability distribution
     * function (pdf):
     *
     *     pdf(t) = 1/meanTime * exp(-t/meanTime) : if t>=0 and 0 otherwise
     */
    public Exponential(long meanTime, TimeUnit unit) {
        meanTimeNs = TimeUnit.NANOSECONDS.convert(meanTime, unit);
        random = new Random();
    }

    /**
     * Returns the relative time of the next arrival (in nanoseconds precision), that is the time
     * between two consecutive requests. This load generator models a Poisson process, hence the
     * time follows an exponential distribution. The formula[1] for each random time (t) is the
     * following:
     *
     *     t = -meanTime * log(1 - random)
     *
     * where `random` represents a randomly generated number equally distributed within the [0,1]
     * interval.
     *
     * Reference:
     *
     * [1] <http://en.wikipedia.org/wiki/Inverse_transform_sampling>
     */
    @Override
    public long nextRelativeTimeNs(){
        return (long) (-meanTimeNs * Math.log(1 - random.nextDouble()));
    }

    /**
     * Returns a brief info about this Load generator. It gives the load type as Poisson process and
     * the mean arrival time in ms.
     */
    @Override
    public String toString() {
        return "Poisson process with mean arrival time of " +
                (double) meanTimeNs / 1_000_000d + " ms";
    }
}
