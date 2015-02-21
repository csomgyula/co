package co.experiment;

import co.Experiment;
import co.Load;
import co.Task;
import co.load.Steady;
import co.task.Fibonacci;

import java.util.concurrent.TimeUnit;

/**
 * Experiment with Steady load and Fibonacci Task.
 */
public class SteadyFibonacci extends Experiment {
    private Steady load;
    private long minTime, maxTime, deltaTime;
    private TimeUnit timeUnit;

    private int fibonacciIndex;
    private Task task;

    private int requestCount, warmupCount;

    @Override
    public Task getTask() {
        if (task == null) {
            task = new Fibonacci(fibonacciIndex);
        }
        return task;
    }

    @Override
    public Load getLoad(long time, TimeUnit unit) {
        if (load == null) {
            load = new Steady(time, unit);
        }
        else {
            load.setTimeDiff(time, unit);
        }
        return load;
    }

    public int getFibonacciIndex() {
        return fibonacciIndex;
    }

    public void setFibonacciIndex(int fibonacciIndex) {
        this.fibonacciIndex = fibonacciIndex;
    }

    public static void main(String[] args) {
        SteadyFibonacci experiment = new SteadyFibonacci();

        // init Load
        experiment.setMinTime(1000);
        experiment.setMaxTime(1100);
        experiment.setDeltaTime(10);
        experiment.setTimeUnit(TimeUnit.MICROSECONDS);

        // init Task
        experiment.setFibonacciIndex(1_000_000);

        // init Benchmark counts
        experiment.setWarmupCount(1000);
        experiment.setRequestCount(10000);

        // run experiment
        experiment.run();
    }
}
