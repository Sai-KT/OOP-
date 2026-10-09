package com.college.sms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ==============================================================================
 * AppConfiguration (OOP Concept: Singleton Design Pattern & Configuration)
 * ==============================================================================
 * Central application configuration class.
 * Demonstrates:
 * 1. Singleton Pattern: Managed as a singleton instance within the Spring Inversion of
 *    Control (IoC) container.
 * 2. Cross-Origin Resource Sharing (CORS): Permits development testing across
 *    local servers while embedding the static frontend seamlessly.
 * ==============================================================================
 */
@Configuration
public class AppConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
