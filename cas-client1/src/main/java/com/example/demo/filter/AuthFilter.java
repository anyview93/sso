package com.example.demo.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

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
    public static final String CLIENT_USER = "client.user";
    public static final String SSO_TICKET = "sso.ticket";

    @Value("${sso.server}")
    private String ssoServer;
    @Value("${sso.service}")
    private String service;
    @Autowired
    private RestTemplate restTemplate;
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        if(!(req instanceof HttpServletRequest) || !(resp instanceof HttpServletResponse)){
            chain.doFilter(req, resp);
            return;
        }
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String url = request.getRequestURL().toString();
        if(url.startsWith(service +"/index") || url.contains("loginCallBack") ||  url.contains("login")){
            chain.doFilter(req, resp);
            return;
        }
        HttpSession session = request.getSession();
        if(null == session){
            response.sendRedirect(ssoServer + "/ssoServer?service=" + service);
            return;
        }
        Object clientUser = session.getAttribute(CLIENT_USER);
        if(null != clientUser){
            doFilter(request,response,chain);
            return;
        }
        Object ssoUser = session.getAttribute(SSO_USER);
        if(null == ssoUser){
            String ticket =request.getParameter(SSO_TICKET);
            if(!StringUtils.isEmpty(ticket)){
                response.sendRedirect(ssoServer + "/checkTicket?service=" + service + "&" + SSO_TICKET + "=" +ticket);
                return;
            }
            response.sendRedirect(ssoServer + "/ssoServer?service=" + service);
            return;
        }
        session.setAttribute(CLIENT_USER,ssoUser);
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
