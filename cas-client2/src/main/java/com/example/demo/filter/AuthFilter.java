package com.example.demo.filter;

import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AuthFilter implements Filter {

    public static final String SSO_USER = "sso.user";
    public static final String CLIENT_USER = "client.user";
    public static final String SSO_TICKET = "ticket";

    @Value("${sso.server}")
    private String ssoServer;
    @Value("${sso.service}")
    private String service;
    @Value("${sso.nofilter")
    private String nofilter;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        if (!(req instanceof HttpServletRequest) || !(resp instanceof HttpServletResponse)) {
            chain.doFilter(req, resp);
            return;
        }
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (isRequestUrlExcluded(request)) {
            chain.doFilter(req, resp);
            return;
        }

        HttpSession session = request.getSession(false);
        final User user = (session != null) ? (User) session.getAttribute(SSO_USER) : null;
        System.out.println("======>>" + user);
        if (user != null) {
            chain.doFilter(request, response);
            return;
        }

        final String ticket = request.getParameter(SSO_TICKET);
        System.out.println("=====>" + ticket);
        if (!StringUtils.isEmpty(ticket)) {
            chain.doFilter(request, response);
            return;
        }

        String encode = URLEncoder.encode(service, "UTF-8");
        response.sendRedirect(ssoServer + "/login?service=" + encode);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {

    }

    private boolean isRequestUrlExcluded(HttpServletRequest request) {

        String uri = request.getRequestURI();
        String[] split = nofilter.split(",");
        for (int i = 0; i < split.length; i++) {
            if (uri.endsWith(split[i])) {
                return true;
            }
        }
        return false;
    }

}
