/**
 * 
 */
package com.example.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * LoginController.java
 * 
 *
 * @author shizhiguo
 * @date 2018年11月26日
 */
@Controller
public class LoginController {

	private static Map<String, String> user = new HashMap<>();
	private static Map<String, String> map = new HashMap<>();
	static {
		user.put("user", "123");
	}
	
	@RequestMapping("/login")
	public void index(HttpServletRequest request,HttpServletResponse response, String name) throws IOException {
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
		Cookie[] cookies = request.getCookies();
		String service = request.getParameter("service");
		if (null != cookies){
			for (Cookie cookie : cookies) {
				if("casServer".equals(cookie.getName())) {
					response.sendRedirect(service + "/loginCallBack" + "?ticket=123456789");
					return;
				}
			}
		}
		response.sendRedirect("http://localhost:8080/cas-server/login");
	}
	
	@RequestMapping("/checkTicket")
	public void checkTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String ticket = request.getParameter("ticket");
		String service = request.getParameter("service");
		if("123456789".equals(ticket)) {
			request.getSession().setAttribute("user","user");
			response.sendRedirect(service + "/login" + "?user=user");
			return;
		}
		response.sendRedirect("http://localhost:8080/cas-server/login");
	}
}
