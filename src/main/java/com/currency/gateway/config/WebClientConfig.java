package com.currency.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${fixer.api.endpoint}")
    private String fixerApiEndpoint;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().baseUrl(fixerApiEndpoint);
    }
}
