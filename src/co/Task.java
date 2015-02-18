package co;

/**
 * Represents the task interface for the tasks to be benchmarked. Has some support to prevent
 * dead code elimination.
 */
public interface Task {
    
    /**
     * Executes the logic represented by the Task. The return object is just a trick in order to
     * avoid dead code elimination. The returned object will be passed to the Stat component,
     * hence it will make it more difficult to detect dead code.
     *
     * See:
     *
     * - <http://en.wikipedia.org/wiki/Dead_code>
     * - <http://trask.github.io/pjug-jmh/#1>
     * - <http://www.javaworld.com/article/2076060/build-ci-sdlc/compiler-optimizations.html>
     * - <http://stackoverflow.com/questions/23817000/java-constant-expressions-and-code-elimination>
     * - <http://stackoverflow.com/questions/23068734/can-the-java-compiler-eliminate-dead-code-from-the-following-scenarios>
     * - <http://www.oracle.com/technetwork/articles/java/architect-benchmarking-2266277.html>
     * - <http://daniel.mitterdorfer.name/articles/2014/benchmarking-flaws/>
     */
    public Object execute();
}