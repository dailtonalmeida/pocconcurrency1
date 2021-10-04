
package br.nom.dsa.pocconcurrency1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PocJavaConcurrencyApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(PocJavaConcurrencyApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PocJavaConcurrencyApplication.class, args);
        LOGGER.info("this is the main method!!!");
    }

}
