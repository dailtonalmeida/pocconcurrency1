
package br.nom.dsa.pocconcurrency1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author dailtonalmeida
 */
@RestController
public class HomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    private static final int N = 10;
    private final ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
    private final ExecutorService LARGE_THREAD_EXECUTOR = Executors.newFixedThreadPool(2 * N);
    private final ExecutorService SMALL_THREAD_EXECUTOR = Executors.newFixedThreadPool(N / 3);
    private final EchoClient echoClient;

    public HomeController(EchoClient echoClient) {
        this.echoClient = echoClient;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> home() {
        return Collections.singletonMap("minha chave", "meu valor");
    }

    
    
    @GetMapping(value = "/echo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> echo(@RequestParam(value = "input", defaultValue = "World") String input) {
        long t0 = System.currentTimeMillis();
        MyTask.doSlowTask();
        long t1 = System.currentTimeMillis();
        return Collections.singletonMap("echo", input + " in " + (t1 - t0) + " seconds");
    }

    @GetMapping(value = "/echoclient", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> echoclient(@RequestParam(value = "nTimes", defaultValue = "1") int nTimes) {
        return echoClient.callEchoServiceSomeTimes(nTimes);
    }

    
    
    @GetMapping(value = "/sequencial", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> sequencial() {
        long t0 = System.currentTimeMillis();
        IntStream.range(0, N).forEach(n -> MyTask.doSlowTask());
        long t1 = System.currentTimeMillis();
        return Collections.singletonMap("elapsed time", (t1 - t0) + " seconds");
    }

    
    
    @GetMapping(value = "/threads", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> threads() {
        List<Thread> tasks = MyRunnable.buildListOfThreads(N);
        
        long t0 = System.currentTimeMillis();

        for (Thread t: tasks) {
            t.start(); //NAO EH O METODO RUN!!!
        }
        for (Thread t: tasks) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        long t1 = System.currentTimeMillis();
        return Collections.singletonMap("elapsed time", (t1 - t0) + " seconds");
    }

    
    private Map<String, String> doWithExecutorService(ExecutorService executor) {
        //cria e executa N callables atraves do executor
        List<MyCallable> callables = MyCallable.buildCallables(N);

        long t0 = System.currentTimeMillis();

        try {//CHAMA TODOS OS CALLABLES
            List<Future<Integer>> futures = executor.invokeAll(callables);

            List<Integer> results = new ArrayList<>();
            for (Future<Integer> future: futures) {
                results.add(future.get());
            }
//            executor.shutdown();
//            executor.awaitTermination(15, TimeUnit.SECONDS);

            LOGGER.info("results: " + results);
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        long t1 = System.currentTimeMillis();
        return Collections.singletonMap("elapsed time", (t1 - t0) + " seconds");
    }

    
    
    @GetMapping(value = "/executorsinglethread", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> executorsinglethread() {
        return doWithExecutorService(SINGLE_THREAD_EXECUTOR);
    }

    
    
    @GetMapping(value = "/executorfixeddouble", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> executorfixeddouble() {
        return doWithExecutorService(LARGE_THREAD_EXECUTOR);
    }

    
    
    @GetMapping(value = "/executorfixedfraction", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> executorfixedfraction() {
        return doWithExecutorService(SMALL_THREAD_EXECUTOR);
    }
}
