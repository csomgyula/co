TODO
==

Updated: 2015.02.19.

* Split Stat by factoring out Recording from Stat => Benchmark run will only depend on Recording
* Argument handling in main method

Backlog
--

* Document experimental results
* Rethink warm up logic

DONE
--

* Correction Scheme (described in the paper)
* Benchmark runner
* Load interface, Steady load, Poisson process load
* Task interface, Counter task, Fibonacci task, some support against dead code elimination
* Stat interface, Raw stat (idle, wait, dequeue, (gross)processing, service, arrival diffs), Indicator stat (min, max, avg, 99% percentile)