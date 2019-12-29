package com.example.demo.filter;

import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

public class TicketValidateFilter implements Filter {
    public static final String SSO_USER = "sso.user";
    public static final String CLIENT_USER = "client.user";
    public static final String SSO_TICKET = "ticket";
    private static final String SSO_SERVICE = "service";
    private static final String SESSIONID = "sessionId";

    @Value("${sso.server}")
    private String ssoServer;
    @Value("${sso.service}")
    private String service;
    @Value("${sso.nofilter")
    private String nofilter;
    @Autowired
    private RestTemplate restTemplate;

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
        HttpSession session = request.getSession();
        String ticket = request.getParameter(SSO_TICKET);
        if (!StringUtils.isEmpty(ticket)) {
            HashMap<String, String> param = new HashMap<>();
            param.put(SSO_TICKET, ticket);
            param.put(SSO_SERVICE, service);
            param.put(SESSIONID, session.getId());
            try {
                final User user = this.validate(ssoServer + "/validateTicket", param);
                session.setAttribute(SSO_USER, user);
                LogoutFilter.sessions.putIfAbsent(ticket,session);
            } catch (Exception e) {
                System.out.println("=====>>ticket校验失败");
                response.sendRedirect("http://www.baidu.com");
            }
        }
        chain.doFilter(request, response);
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

    private User validate(String url, Object param) throws Exception {
        User user = restTemplate.postForObject(url, param, User.class);
        if (null == user) {
            throw new NullPointerException();
        }
        return user;
    }
}
