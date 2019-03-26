/*
package com.example.demo.utils;


import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.SimpleCacheManager;

public final class CacheUtils {

    public static volatile CacheManager cacheManager;

    private CacheUtils() {
        super();
    }

    public static Cache getSessionCache(){
        Cache cache = getCacheManager().getCache("cas_sessions");
        return cache;
    }

    public static CacheManager getCacheManager(){
        if(null == cacheManager){
            synchronized (CacheUtils.class){
                if(null == cacheManager){
//                    cacheManager = new ConcurrentMapCacheManage();
                }
            }
        }
        return cacheManager;
    }
}
*/
