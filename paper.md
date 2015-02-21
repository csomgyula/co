Correction scheme for Coordinated Omission
==
Gyula Csom

Working draft


1 Introduction
--
In this paper I try to give some insight into Coordinated Omission (a term coined by Gil Tene of Azul [1]) and to introduce a possible correction scheme. The paper is organized as follows:


* Chapter 2 briefly introduces Coordinated Omission and the problem it could cause in benchmark measurements and evaluation. For the curious reader other introductionary materials are also available through the Web [1], [2].
* Chapter 3 introduces the correction scheme. First we revisit the problem by going through an example. Then we restate the problem in a formal manner. At last we introduce the correction scheme and its assumptions.
* Chapter 4 is work in progress. If and when it is done, it will contain the experimental results.

2 Coordinated Omission
--
Coordinated Omission (a phrase coined by Gil Tene of Azul [1]) is a bencmarking problem which can occur if a test loop looks something like this:

    while(testing){
        start = System.nanoTime();
        executeBenchmarkedTask();
        recordTime(System.nanoTime() - start);
        waitUntilNextSecond();
    }

This test loop tries to mimick the real life scenario, when incoming service requests arrive in a steady rate (in this example 1 request per second). However the test loop fails to simulate the real world when a high latency event occurs. In such case the next execution cycle must wait until the high latency task finishes, which then breaks the constant incoming rate: 

Lets say the high latency task runs for 10 seconds. Then the next cycle would start only 10 seconds later. Hence during the event the incoming rate will decrease by 90% (from 1 request/sec to 0.1 request/second) which is quite a big deviation from the real load.

###2.1 The definition

**1 Definition: Coordinated Omission occurs when the request arrival rate is not independent from the request processing rate.** In other words Coordinated Ommision happens when the incoming rate is dependent on the outgoing rate.

Example: One simple case is the following sequential loop of some client:

    Step 1: Send a request to the server
    Step 2: Wait until the server processes it
    Step 3: Goto to Step 1

This case the incoming rate is clearly not independent from the outgoing rate: the incoming rate is controlled by the outgoing rate, so that incoming rate becomes the same as the outgoing one.

The original test loop is similar but slightly different: 

* In normal circumstances the test loop runs in an uncoordinated way, it generates requests in a steady rate. 
* However, during high latency events it starts to execute the above sequential logic.

###2.2 The problem
Coordinated Omission generates problem iff the real world (the real distribution of incoming requests) is uncoordinated. In such cases it will tweak the latency statistics. Take the example above:

* During the high latency event, the test loop won't execute new tasks except the high latency one. Hence during this event it won't record anything but one long run.
* In real life, the incoming requests arrive in an uncoordinated way. During the high latency event many new requests will arrive and be blocked until the long run finishes. Since the incoming rate is (approximately) 1 request/sec, the first blocked request will wait (at least) 9 seconds, the second (at least) 8 seconds, etc. the last one (at least) 1 second.

After all the above measurement techinque will underestimate high latency events. Again take the above example:

* The test loop will report only one high latency event of 10 seconds. 
* Meanwhile in real life there will be 9 other high latency events with idle time of (at least) 9, 8, ..., 1 seconds.

###2.3 The goal
There are situations when Coordinated Omission could be avoided, for instance by an external, async load generator [3]. However in this paper we focus on correction rather than avoidance. The latter is a more ambitious goal, however the former might be still useful in some situations (such as legacy benchmarking systems, where rewriting the code is not a viable option). 

In this paper we are going after a nonintrusive technique which does not require the rewriting of the original benchmarking code.

3 Correction
--
As seen Coordinated Omission may tweak latency statistics. Before discussing the correction technique lets revisit the problem by going through a simple example:

###3.1 Revisiting the problem
Lets assume that incoming requests arrive at a steady 1 request / second rate and the benchmark recorded 4 runs with the following processing times:

    1 second, 5 seconds, 3 seconds, 1 second

That is the first and last runs took only 1 second, however the two runs in the middle were high latency ones. 
Due to the steady incoming rate the requests assumed to arrive in a linear fashion, that is they arrived respectively at the:

    0th second, 1st second, 2nd second, 3rd second

Now we can calculate the total latency times for this sample:

1. The first request finished at the 1st second, hence its latency is 1 second equal to its processing time.
1. The second request finished at 1 + 5 = 6th second hence its latency is equal with its processing time, that is 5 seconds.
1. The third request finished at 1 + 5 + 3 = 9th second hence its latency is 9 – 2 = 7 seconds which is larger than its processing time, 3 seconds. The total latency time is larger, because the third request had to wait for the previous one to finish. More specifically it stayed 4 seconds in idle before its processing started.
1. Finally the  last request finished at the 1 + 5 + 3 + 1 = 10th second, hence its total latency is 10 – 3 = 7 seconds. Although its processing time was short, just 1 second, its total latency still became high, again because the request had to stay in idle for 6 seconds.

Probably the above sample illustrated the problem behind Coordinated Omission: 

* The original latency statistics considers processing times only that is: 1 second, 5 seconds, 3 seconds, 1 second. 
* However if requests arrive in an uncordinated way, then one must also add idle times to processing times. Hence we get 1 second, 5 seconds, 7 seconds, 7 seconds, which then yields a different latency statistics.

###3.2 Formal problem statement
In order to expose the formal problem statement, lets introduce some definitions:

**2 Definition**: 

**(i) Active processing time per request** (or processing time in short) is the time while a request is actively processed (ie. it is not idle).

**(ii) Idle time per request** (or idle time in short) is the time while a request stays at the server, but is not actively processed.

**(iii) Total service time per request** (or service time in short) is just the sum of idle time and processing time, that is the total time the request stays at the server:

    Total service time = Idle time + Active processing time

Note that in real contexts, the total service time itself might not accurately measure the total latency. For instance, in a client-server situation one has to measure network round trip time (RTT) as well.

Now, equipped with the above definitions, we can (re)state the problem: 

**3 Theorem: During high loads, total service time and active processing time starts to diverge due to non-zero idle times (with the assumption that requests arrive in an uncoordinated way).** 

Notes:

* High load simply means that incoming rate is higher then processing rate. High load could be just a jitter, or system saturation or exceptional load. The reason for high load is not important, unless guarded against it, Coordinated Ommission will tweak statistics whenever load is higher than service speed.
* Lets recall that the problem occurs when the latency statistics are calculated over processing times. If and when high loads occur, then this kinda calculation will underestimate high latency events.

Probably the sample also shows how to correct the problem in general: 

Instead of just using processing times, we must take into account idle times as well. More specifically we must determine when invidual requests arrived, finished and then the difference between the two will yield the total service time. In greenfield benchmark development this might be easy to implement the above scenario. We just have to track the life cycle of each idividual requests, that is record the arrival time and the finishing time. However the situation might be different with existing code. Recording of the above events might require the modification of the original software. This is where the following correction scheme could help:

###3.3 The correction scheme
The correction scheme takes the following assumptions:

**4 Assumption: The following statistics must be known**:

**(i) Request arrival rate** (not necessarily in advance).

**(ii) Processing times** (which is a natural requirement, since the benchmarking code should record this statistic).

**5 Assumption: Request processing discipline is**

**(i) single tasking**

**(ii) atomic**: After its processing is started a request never goes back to idle;

**(iii) ordered**: Requests are processed in arrival order, that is First-In-First-Out;

**(iv) greedy**: Request processing threads do not go idle unless the request queue is empty.

Notes: 

* In many situations the above assumptions are not likey. There are processing schemes where they simply do not hold - like priority-based scheduling, shortest-task-first scheduling, etc. (see Wikipedia [3] for more details and examples). However in simple benchmarking schemes the above assumptions are quite natural.
* Assumption (ii) and (iii) is redundant. The ordering property is a stronger property than atomicity: atomicity can be derived from the ordering property.

Our last assumption:

**6 Assumption: dequeueing time is negligable**, where dequeueing time means the difference between the time when the request became ready (eligable for processing) and the time when its processing started. 

From atomicity we we can conclude the following theorem:

**7 Theorem: If start times of processing are known, then service times can be calculated as follows**:

    Service time = Start time of processing – Arrival time + Active processing time

Proof: Atomicity means that idle time is the same as Processing start time – Arrival time, that is: `Idle time = Start time of processing – Arrival time`

Before moving on lets introduce some notations:

**8 Notation: Let (i) `arriv_time[N]` denote the known arrival time of the Nth request and (ii) `proc_time[N]` the Nth processing time as measured by the benchmark** (these are the known stats as per Assumption 4). 

**Also let (iii) `start_time[N]` denote the start times of processing and (iv) `serv_time[N]` the service times** (these are the not-yet-known stats).

First restate Theorem 7 using the above notations:

**7’ Theorem**: Using the above notations Theorem 7 states the following: 

    serv_time[N] = start_time[N] - arriv_time[N] + proc_time[N]

Since by Assumption 4 arrival- and processing times are known, all that we have to do is to calculate the processing start times.

Now the main theorem:

**9 Theorem: Start time of the next processing is (almost) equal to the start time of the previous processing + the previous processing time or the arrival time of the next request, whichever is greater**:

    start_time[N+1] = MAX(start_time[N] + proc_time[N], arriv_time[N+1])

Proof: Due to Assumption 5 (i) single tasking, (iv) greediness and (ii) ordering, the next request processing starts as soon as the previous one finished (which is the same as `start_time[N] + proc_time[N]`), except for the case when the next request has not yet arrived (`arriv_time[N+1]`). Due to Assumption 6 if/whenever the next request is/becomes enqueued, then the dequeueing time is negligable, request processing starts (almost) immediately.

By appliing the above theorem we can recursively calculate the start times:

**10 Formula: `start_time[N]` can be recursively calculated as follows**:

    start_time[1]   = MAX(0, arriv_time[1])
    start_time[2]   = MAX(start_time[1] + proc_time[1], arriv_time[2])
    ...
    start_time[N+1] = MAX(start_time[N] + proc_time[N], arriv_time[N+1])

Especially when the incoming rate is a steady one, then we get the following formula:

**10.b. Formula**:
 
    start_time[1]   = MAX(0, arriv_time[1])
    start_time[2]   = MAX(start_time[1] + proc_time[1], adjacent_time)
    ...
    start_time[N+1] = MAX(start_time[N] + proc_time[N], N * adjacent_time )

where `adjacent_time` means the (constant) time difference between two adjacent requests.

Now combining Theorem 7 and 10 we get the corrected formula for total service time.

###3.4 Summary

* We have introduced a possible correction scheme to avoid problems caused by Coordinated Omission. 
* The correction works only with some assumptions: (I) arrival & processing times are known, (II) scheduling is single tasking, atomic, ordered and greedy, (III) dequeueing time is negligable. 
* Also do not forget that if the service runs in a client server context then you should measure round-trip-time as well in order to calculate full latency (response time).
* If the above assumptions hold, then latency statistics can be calculated w/o rewriting the original benchmarking code.


4 Experimental results
--

Lab tests are work in progress: 

I implement a small benchmark suite (not a general purpose one) in order to experiment with Coordinated Omission. The project is hosted on GitHub: <https://github.com/csomgyula/co>

Currently there are only preliminary results:

* **It is worthless to test under extreme loads**. When average load is higher than average processing time, requests starts queueing, which has bigger impact then Coordinated Omission. Hence the practical range of load is somewhere between the average processing time and the maximum processing time.
* Within my environment, the runtime overhead is around 0.1 ms and could reach ~1 ms as a max. Hence **dequeueing time might not be negligable** and should be taken into account as well (ie. in the Correction Scheme).
* **The difference between processing time and "real" latency seems to be higher when the load is higher** (ie. when load is near to the average processing time). This is quite reasonable. For steady loads I've seen 3-4x difference, for Poisson loads even higher ones. However this is currently work in progress...

5 References
--
[1] Matt Schuetze: How NOT to Measure Latency
<http://www.azulsystems.com/sites/default/files/images/HowNotToMeasureLatency_LLSummit_NYC_12Nov2013.pdf>

[2] Attila-Mihaly Balazs: How (NOT TO) measure latency
<http://www.todaysoftmag.com/article/756/how-not-to-measure-latency>

[3] Gil Tene: wrk2 README:

> some completely asynchronous load generators can avoid Coordinated Omission by sending requests without waiting for previous responses to arrive. However, this (asynchronous) technique is normally only effective with non-blocking protocols or single-request-per-connection workloads. When the application being measured may involve mutiple serial request/response interactions within each connection, or a blocking protocol (as is the case with most TCP and HTTP workloads), this completely asynchronous behavior is usually not a viable option.
https://github.com/giltene/wrk2 

<https://github.com/giltene/wrk2>

[4] Wikipedia article on Service disciplines <http://en.wikipedia.org/wiki/Queueing_theory#Service_disciplines>

