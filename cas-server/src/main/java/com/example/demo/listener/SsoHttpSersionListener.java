package com.example.demo.listener;

import com.example.demo.common.CacheEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听session销毁动作
 */
@WebListener("ssoHttpSersionListener")
public class SsoHttpSersionListener implements HttpSessionListener {

    @Autowired
    private CacheManager cacheManager;

    private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();


    @Override
    public void sessionCreated(HttpSessionEvent se) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Cache cache = cacheManager.getCache(CacheEnum.SESSIONS.name());
        HttpSession session = se.getSession();
        cache.evict(session.getId());
    }
}
