package com.toda.api.TODASERVERSPRINGBOOT.config;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import lombok.RequiredArgsConstructor;

import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {
    @Bean
    public PoolingHttpClientConnectionManager connectionManager(){
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(20);
        return connectionManager;
    }

    @Bean
    public RestTemplate RestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(20);

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();

        factory.setHttpClient(httpClient);
        factory.setConnectTimeout(2000);
        return new RestTemplate(factory);
    }
}
