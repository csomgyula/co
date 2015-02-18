package co.task;

import co.Task;

/** 
 * A simple task to illustrate the situation when dequeueing time is not negligible. 
 * This task is so fast that is comparable to the runtime overhead of the benchmarking logic.
 */ 
public class Counter implements Task {
    private int count;

    /**
     * Executes the logic represented by the Task, this case simply increment a counter.
     */
    @Override
    public Object execute() { return count++; }
}