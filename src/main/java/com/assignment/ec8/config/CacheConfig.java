package com.assignment.ec8.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Konfigurasi cache provider menggunakan ConcurrentMapCache (in-memory sederhana).
 * Cache yang tersedia:
 * - "products"     : hasil GET /api/products (list)
 * - "productById"  : hasil GET /api/products/{id} (detail per id)
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(List.of("products", "productById"));
        return cacheManager;
    }
}
