package com.ak.rfp.config;  // Adjust to your package

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration  // Tells Spring this is a config class
public class WebClientConfig {

    @Bean  // Registers as Spring bean—now injectable
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();  // Default builder with timeouts, etc.
    }

    // Optional: Customize (e.g., global timeout)
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .defaultHeader("User-Agent", "RFP-App/1.0")  // Example header
                .build();
    }
}