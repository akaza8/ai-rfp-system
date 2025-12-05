package com.ak.rfp.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  // Tells Spring this is config
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Allow all endpoints (or "/api/**" for specific)
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")  // Your frontend URLs (add more if needed)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")  // HTTP methods
                .allowedHeaders("*")  // All headers (e.g., Authorization)
                .allowCredentials(true);  // If using cookies/auth
    }
}