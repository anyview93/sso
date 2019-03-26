package com.example.demo.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

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
                    cacheManager = CacheManager.create();
                }
            }
        }
        return cacheManager;
    }
}
