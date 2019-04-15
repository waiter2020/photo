package com.upc.photo.config;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.stereotype.Component;

/**
 * @Author: waiter
 * @Date: 2019/4/15 11:17
 * @Version 1.0
 */

public class MyRedisCache extends RedisCache {
    private final RedisCacheWriter cacheWriter;
    private final String name;
    /**
     * Create new {@link RedisCache}.
     *
     * @param name        must not be {@literal null}.
     * @param cacheWriter must not be {@literal null}.
     * @param cacheConfig must not be {@literal null}.
     */
    protected MyRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
        this.cacheWriter=cacheWriter;
        this.name=name;
    }


    @Override
    public void evict(Object key) {
        cacheWriter.clean(name, createAndConvertCacheKey(key));
    }


    private byte[] createAndConvertCacheKey(Object key) {
        return serializeCacheKey(createCacheKey(key));
    }
}
