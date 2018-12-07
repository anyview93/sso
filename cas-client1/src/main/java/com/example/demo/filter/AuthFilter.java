package com.example.demo.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@WebFilter(filterName = "AuthFilter",urlPatterns = "/*")
public class AuthFilter implements Filter {

    public static final String SSO_USER = "sso.user";
    public static final String SSO_TICKET = "sso.ticket";

    @Value("${sso.server}")
    private String ssoServer;
    @Value("${sso.service}")
    private String service;
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        if(!(req instanceof HttpServletRequest) || !(resp instanceof HttpServletResponse)){
            chain.doFilter(req, resp);
            return;
        }
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession();
        if(null == session){
            response.sendRedirect(ssoServer + "/ssoServer?service=" + service);
            return;
        }
        Object user = session.getAttribute(SSO_USER);
        if(null == user){
            String ticket =request.getParameter(SSO_TICKET);
            if(!StringUtils.isEmpty(ticket)){
                response.sendRedirect(ssoServer + "/checkTicket?service=" + service + "&" + SSO_TICKET + "=" +ticket);
                return;
            }
            response.sendRedirect(ssoServer + "/ssoServer?service=" + service);
            return;
        }
        session.setAttribute(SSO_USER,user);
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
