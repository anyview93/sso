package com.example.demo.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.example.demo.entity.User;

public class LogoutFilter implements Filter {

    public static final String SSO_USER = "sso.user";
    public static final String CLIENT_USER = "client.user";
    public static final String SSO_TICKET = "ticket";
    private static final String SSO_SERVICE = "service";
    private static final String SESSIONID = "sessionId";
    private static final String SSO_TGC = "CAS_TGC";

    @Value("${sso.server}")
    private String ssoServer;
    @Value("${sso.service}")
    private String service;
    @Value("${sso.logout.filters")
    private String filters;

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

        if (!isRequestUrlFilter(request)) {
            chain.doFilter(req, resp);
            return;
        }
        String tgt = getTgt(request);
        //调用sso server的logout接口

        //注销本服务的session
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
    private boolean isRequestUrlFilter(HttpServletRequest request) {

        String uri = request.getRequestURI();
        String[] split = filters.split(",");
        for (int i = 0; i < split.length; i++) {
            if (uri.endsWith(split[i])) {
                return true;
            }
        }
        return false;
    }

    private String getTgt(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String tgt = "";
        if (null != cookies && cookies.length > 0) {
            for (int i = 0; i < cookies.length; i++) {
                if (SSO_TGC.equals(cookies[i].getName())) {
                    tgt = cookies[i].getValue();
                    break;
                }
            }
        }
        return tgt;
    }
}
