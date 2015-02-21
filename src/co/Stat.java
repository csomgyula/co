package co;

import co.stat.Indicators;
import co.stat.Raw;

/**
 * Process statistics.
 *
 * FEATURES:
 *
 * - Calculate Raw statistics
 * - Calculate Indicators
 *
 * RELATIONS:
 *
 * - Stat processing is called by the Benchmark when it is done executing the benchmark.
 * - Stat delegates to Raw and Inditcators in order to do the heavy lifting.
 */
public class Stat {
    /**
     * Process statistics after benchmark execution is done.
     */
    public void process(Recording recording, boolean export) {
        Raw raw = new Raw(recording);
        raw.calculate();

        Indicators indicators = new Indicators(raw);
        indicators.calculate();
        indicators.printOut();

        if (export) {
            raw.toCSV("raw_stat.csv");
        }
    }
}
