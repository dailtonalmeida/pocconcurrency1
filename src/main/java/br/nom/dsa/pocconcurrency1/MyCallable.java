
package br.nom.dsa.pocconcurrency1;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dailtonalmeida
 */
public class MyCallable implements Callable<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyCallable.class);
    private int n;

    public MyCallable(int n) {
        this.n = n;
    }

    @Override
    public Integer call() throws Exception {
        MyTask.doSlowTask();
        Integer result = 2 * n;
        LOGGER.info("callable " + n + " => " + result + " done!!! " + Thread.currentThread().getName());
        return result;
    }

    public static List<MyCallable> buildCallables(int n) {
        return IntStream.range(0, n)
                .mapToObj(MyCallable::new)
                .collect(Collectors.toList());
    }    
}
