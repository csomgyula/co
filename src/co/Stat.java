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
 */
public class Stat {
    /**
     * Process statistics after the benchmark execution is done.
     */
    public void process(Recording recording, boolean export) throws Exception {
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
