package com.example.demo.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogoutFilter implements Filter {

    public static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    @Value("${sso.server}")
    private String ssoServer;

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

        String ticket = request.getParameter(AuthFilter.SSO_TICKET);
        if(StringUtils.isEmpty(ticket)){
            //调用sso server的logout接口
            response.sendRedirect(ssoServer + "/logout");
        }else {
            HttpSession session = LogoutFilter.sessions.remove(ticket);
            if(null != session){
                session.invalidate();
            }
        }


    }

    @Override
    public void init(FilterConfig config) throws ServletException {

    }

}
