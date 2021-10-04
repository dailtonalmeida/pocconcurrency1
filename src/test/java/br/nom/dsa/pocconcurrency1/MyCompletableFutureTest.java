/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.nom.dsa.pocconcurrency1;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import static org.assertj.core.api.Assertions.*;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author dnse
 */
public class MyCompletableFutureTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyCompletableFutureTest.class);

    @Test
    @Ignore
    public void testGet() throws Exception {
        Supplier<Integer> supplier = () -> 10;

        CompletableFuture<Integer> firstCompletableFuture = CompletableFuture.supplyAsync(supplier);

        Integer result = firstCompletableFuture.get();
        LOGGER.info("testGet result {}", result);
        assertThat(result).isEqualTo(10);
    }

    @Test
    @Ignore
    public void testThenCombine() throws Exception {
        LOGGER.info("testThenCombine start");

        Supplier<String> supplier1 = () -> {
            MyTask.doSlowTask();
            MyTask.doSlowTask();
            MyTask.doSlowTask(); //3X MESMO
            LOGGER.info("testThenCombine supplier1");
            return "a";
        };
        Supplier<String> supplier2 = () -> {
            MyTask.doSlowTask();
            MyTask.doSlowTask(); //2X MESMO
            LOGGER.info("testThenCombine supplier2");
            return "b";
        };
        BiFunction<String, String, String> resultCombiner = (result1, result2) -> result1 + " x " + result2;
        
        CompletableFuture<String> firstCompletableFuture = CompletableFuture.supplyAsync(supplier1);
        CompletableFuture<String> secondCompletableFuture = CompletableFuture.supplyAsync(supplier2);
        // os dois completable futures acima sao independentes, executados em paralelo e o resultado eh combinado depois
        CompletableFuture<String> thirdCompletableFuture = firstCompletableFuture
                .thenCombine(secondCompletableFuture, resultCombiner);

        String result = thirdCompletableFuture.get();
        LOGGER.info("testThenCombine result {}", result);
        assertThat(result).isEqualTo("a x b");
    }

    @Test
    @Ignore
    public void testThenApply() throws Exception {
        LOGGER.info("testThenApply start");

        Supplier<Integer> supplier = () -> {
            MyTask.doSlowTask();
            LOGGER.info("testThenApply supplier");
            return 10;
        };
        Function<Integer, Integer> function = n -> {
            MyTask.doSlowTask(); //esta funcao nao deveria ter delays previstos
            LOGGER.info("testThenApply function");
            return 2 * n;
        };

        CompletableFuture<Integer> firstCompletableFuture = CompletableFuture.supplyAsync(supplier);
        //o segundo completable future abaixo eh SINCRONO depois que o primeiro executa
        //usar quando a function eh "barata" ou instantanea
        CompletableFuture<Integer> secondCompletableFuture = firstCompletableFuture.thenApply(function);

//        Integer result = 0;
        Integer result = secondCompletableFuture.get();
        LOGGER.info("testThenApply result {}", result);
        assertThat(result).isEqualTo(20);
    }

    @Test
//    @Ignore
    public void testThenCompose() throws Exception {
        LOGGER.info("testThenCompose start");

        Supplier<Integer> supplier = () -> {
            MyTask.doSlowTask();
            LOGGER.info("testThenCompose after slow task");
            return 10;
        };
        Function<Integer, CompletableFuture<Integer>> function = n -> CompletableFuture.supplyAsync(() -> {
            MyTask.doSlowTask();
            LOGGER.info("testThenCompose function");
            return 2 * n;
        });

        CompletableFuture<Integer> firstCompletableFuture = CompletableFuture.supplyAsync(supplier);
        //o segundo completable future abaixo eh ASSINCRONO depois que o primeiro executa
        CompletableFuture<Integer> secondCompletableFuture = firstCompletableFuture.thenCompose(function);

//        Integer result = 0;
        Integer result = secondCompletableFuture.get();
        LOGGER.info("testThenCompose result {}", result);
        assertThat(result).isEqualTo(20);
    }
}
