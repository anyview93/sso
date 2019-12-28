package com.example.demo.config;

import com.example.demo.common.CacheEnum;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author shizhiguo
 * @ClassName CacheManagerConfig
 * @date 2019-03-26
 */
@Configuration
public class CacheManagerConfig {
    @Bean("cacheManager")
    // List<Cache>会主动搜索Cache的实现bean，并添加到caches中
    public SimpleCacheManager cacheManager(List<Cache> caches) {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();

        simpleCacheManager.setCaches(caches);
        return simpleCacheManager;
    }


    @Bean
    public ConcurrentMapCacheFactoryBean sessions() {
        ConcurrentMapCacheFactoryBean sessions = new ConcurrentMapCacheFactoryBean();
        // sessions，则需要添加这样的一个bean
        sessions.setName(CacheEnum.SESSIONS.name());

        return sessions;
    }

}
