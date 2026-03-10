package com.analiso.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCache analysisCache = new CaffeineCache(
            "companyAnalysis",
            Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(6, TimeUnit.HOURS)
                .recordStats()
                .build()
        );

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(analysisCache));
        return manager;
    }
}
