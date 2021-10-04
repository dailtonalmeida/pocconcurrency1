
package br.nom.dsa.pocconcurrency1;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dailtonalmeida
 */
public class MyRunnable implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyRunnable.class);
    private int n;

    public MyRunnable(int n) {
        this.n = n;
    }

    @Override
    public void run() {
        MyTask.doSlowTask();
        LOGGER.info("task " + n + " done!!! " + Thread.currentThread().getName());
    }

    public static List<Thread> buildListOfThreads(int n) {
        return IntStream.range(0, n)
                .mapToObj(MyRunnable::new)
                .map(Thread::new)
                .collect(Collectors.toList());
    }
}
