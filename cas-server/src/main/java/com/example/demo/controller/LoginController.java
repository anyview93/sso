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
	public String index(HttpServletRequest request,HttpServletResponse response, String name, String password) throws IOException {
		if(!"user".equals(name)) {
			return "login";
		}
		String service = request.getParameter("service");
		response.addCookie(new Cookie("casServer", "casServer"));
		response.sendRedirect(service + "/loginCallBack" + "?ticket=123456789");
		return null;
	}
	
	@RequestMapping("/ssoServer")
	public String ssoServer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Cookie[] cookies = request.getCookies();
		String service = request.getParameter("service");
		for (Cookie cookie : cookies) {
			if("casServer".equals(cookie.getName())) {
				response.sendRedirect(service + "/loginCallBack" + "?ticket=123456789");
				return null;
			}
		}
		return "login";
	}
	
	@RequestMapping("/checkTicket")
	public String checkTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String ticket = request.getParameter("ticket");
		String service = request.getParameter("service");
		if("123456789".equals(ticket)) {
			response.sendRedirect(service + "/checkCallBack" + "?user=user");
			return null;
		}
		return "login";
	}
}
