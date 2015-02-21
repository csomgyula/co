package co;

import co.stat.BenchmarkIndicators;
import co.stat.BenchmarkRaw;

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
public class BenchmarkStat {
    /**
     * Process statistics after benchmark execution is done.
     */
    public BenchmarkIndicators process(BenchmarkRecording recording, boolean export) {
        BenchmarkRaw raw = new BenchmarkRaw(recording);
        raw.calculate();

        BenchmarkIndicators indicators = new BenchmarkIndicators(raw);
        indicators.calculate();
        indicators.printOut();

        if (export) {
            raw.toCSV("raw_stat.csv");
        }

        return indicators;
    }
}
