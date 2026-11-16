package org.example.createapi.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RuleCacheManager implements CacheManager {

    private static final int MAX_ENTRIES = 1000;
    private static final long TTL_MINUTES = 10;

    private final List<CaffeineCache> caches = new ArrayList<>();

    public RuleCacheManager() {
        caches.add(new CaffeineCache("transactionCount",
                Caffeine.newBuilder()
                        .maximumSize(MAX_ENTRIES)
                        .expireAfterWrite(Duration.ofMinutes(TTL_MINUTES))
                        .build()));

        caches.add(new CaffeineCache("transactionSum",
                Caffeine.newBuilder()
                        .maximumSize(MAX_ENTRIES)
                        .expireAfterWrite(Duration.ofMinutes(TTL_MINUTES))
                        .build()));

        caches.add(new CaffeineCache("depositWithdrawCompare",
                Caffeine.newBuilder()
                        .maximumSize(MAX_ENTRIES)
                        .expireAfterWrite(Duration.ofMinutes(TTL_MINUTES))
                        .build()));
    }

    @Override
    public CaffeineCache getCache(String name) {
        return caches.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<String> getCacheNames() {
        return caches.stream().map(CaffeineCache::getName).toList();
    }
}
