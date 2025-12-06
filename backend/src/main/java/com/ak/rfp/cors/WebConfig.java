package com.ak.rfp.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  // Tells Spring this is config
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = getAllowedOrigins();
        registry.addMapping("/**")  // Allow all endpoints (or "/api/**" for specific)
//                .allowedOrigins("http://localhost:3000", "http://localhost:5173")  // Your frontend URLs (add more if needed)
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")  // HTTP methods
                .allowedHeaders("*")  // All headers (e.g., Authorization)
                .allowCredentials(true)  // If using cookies/auth
                .maxAge(3600);
    }
    private String[] getAllowedOrigins() {
        String env = System.getenv("ENVIRONMENT");

        if ("production".equals(env)) {
            // Production URLs
            return new String[]{
                    "https://your-frontend-url.onrender.com",
                    "https://your-app-domain.com"
            };
        } else {
            // Development URLs
            return new String[]{
                    "http://localhost:3000",
                    "http://localhost:5173",
                    "http://127.0.0.1:5173"
            };
        }
    }
}