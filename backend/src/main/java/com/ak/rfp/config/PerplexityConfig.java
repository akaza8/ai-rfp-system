package com.ak.rfp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "perplexity.api")
public class PerplexityConfig {

    private String key;
    private String model;
    private String baseUrl;
    private int timeoutSeconds;

    // Getters and Setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public String toString() {
        return "PerplexityConfig{" +
                "model='" + model + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", timeoutSeconds=" + timeoutSeconds +
                '}';
    }
}
