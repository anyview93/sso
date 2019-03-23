/**
 * 
 */
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * LoginController.java
 * 
 *
 * @author shizhiguo
 * @date 2018年11月26日
 */
@Controller
public class LoginController {
	public static final String SSO_USER = "sso.user";
	public static final String SSO_TICKET = "sso.ticket";
	private static Map<String, String> user = new HashMap<>();
	private static Map<String, String> map = new HashMap<>();
	static {
		user.put("user", "123");
	}
	
	@RequestMapping("/login")
	public void index(HttpServletRequest request,HttpServletResponse response, String name) throws IOException {
		System.out.println("======>>server-login");
		String service = request.getParameter("service");
		if(!"user".equals(name) && null != service && "".equals(service)) {
			response.sendRedirect(service + "/loginCallBack" + "?ticket=123456789");
			return;
		}
		response.addCookie(new Cookie("casServer", "casServer"));
		response.sendRedirect(service + "/loginCallBack" + "?ticket=123456789");
	}
	
	@RequestMapping("/ssoServer")
	public void ssoServer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("======>>server-ssoServer");
		Cookie[] cookies = request.getCookies();
		String service = request.getParameter("service");
		if (null != cookies){
			for (Cookie cookie : cookies) {
				if("casServer".equals(cookie.getName())) {
					response.sendRedirect(service + "/loginCallBack?" + SSO_TICKET + "=123456789");
					return;
				}
			}
		}
		response.sendRedirect("http://localhost:8080/cas-server/login");
	}
	
	@RequestMapping("/checkTicket")
	public void checkTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("======>>server-checkTicket");
		String ticket = request.getParameter(SSO_TICKET);
		String service = request.getParameter("service");
		if("123456789".equals(ticket)) {
			request.getSession().setAttribute(SSO_USER,"user");
			response.sendRedirect(service + "/login?"+ SSO_USER + "=user");
			return;
		}
		response.sendRedirect("http://localhost:8080/cas-server/login");
	}
}
