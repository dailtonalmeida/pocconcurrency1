package br.nom.dsa.pocconcurrency1;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

/**
 *
 * @author dailtonalmeida
 */
@Service
public class EchoClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoClient.class);
    private final RestOperations restOperations;

//    @Autowired
    public EchoClient(@Qualifier("restOperations") RestOperations restOperations) {
        LOGGER.info("Using RestOperations {} ...", restOperations);
        this.restOperations = restOperations;
    }

    private Runnable buildRunnable(int n) {
        return () -> {
            ResponseEntity<Map> responseEntity = restOperations.getForEntity("http://localhost:8080/echo?input=" + n, Map.class);
            LOGGER.info("HTTP Status Code {} Retorno {} Thread {}", responseEntity.getStatusCode(), responseEntity.getBody(), Thread.currentThread().getName());
        };
    }

    private Callable<Map> buildCallable(int n) {
        return () -> {
            ResponseEntity<Map> responseEntity = restOperations.getForEntity("http://localhost:8080/echo?input=" + n, Map.class);
            Map result = responseEntity.getBody();
            LOGGER.info("HTTP Status Code {} Retorno {} Thread {}", responseEntity.getStatusCode(), result, Thread.currentThread().getName());
            return result;
        };
    }

    public Map<String, String> callEchoServiceSomeTimes(int nTimes) {
//        List<Thread> tasks = IntStream.range(0, nTimes)
//                .mapToObj(this::buildRunnable)
//                .map(Thread::new)
//                .collect(Collectors.toList());

        List<Callable<Map>> callables = IntStream.range(0, nTimes)
                .mapToObj(this::buildCallable)
                .collect(Collectors.toList());
        
        ExecutorService executor = Executors.newFixedThreadPool(nTimes);
        long t0 = System.currentTimeMillis();

//        for (Thread t: tasks) {
//            t.start();
//        }
//        for (Thread t: tasks) {
//            try {
//                t.join();
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//        }
        try {
            List<Future<Map>> futures = executor.invokeAll(callables);
            for (Future<Map> future: futures) {
                future.get(); //foca que espere todos os callables serem executados
            }
            executor.shutdown();
            executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        long t1 = System.currentTimeMillis();
        return Collections.singletonMap("elapsed time", (t1 - t0) + " seconds");
        
    }
}
