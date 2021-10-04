
package br.nom.dsa.pocconcurrency1.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author dailtonalmeida
 */
@Configuration
public class WebConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

    @Bean("restOperations")
    public RestOperations restOperations() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setMaxConnPerRoute(2)
//                .setMaxConnTotal(2)
//                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
//        requestFactory.setConnectionRequestTimeout(3000);
        requestFactory.setConnectTimeout(15000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        LOGGER.info("Criando RestOperations {} ...", restTemplate);
        return restTemplate;
    }
}
