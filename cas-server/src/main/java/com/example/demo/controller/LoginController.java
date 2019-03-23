/**
 * 
 */
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
		if("user".equals(name) && null != service && "".equals(service)) {
			HttpSession session = request.getSession();
			session.setAttribute(SSO_USER, name);
			response.sendRedirect(service + "/loginCallBack" + "?ticket=123456789");
			return;
		}
		response.sendRedirect(service + "/loginCallBack" + "?ticket=123456789");
	}
	
	@RequestMapping("/ssoServer")
	public void ssoServer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("======>>server-ssoServer");
		HttpSession session = request.getSession();
		Object ssoUser = session.getAttribute(SSO_USER);
		if(null == ssoUser){
			response.sendRedirect("http://localhost:8080/cas-server/login");
			return;
		}
		String service = request.getParameter("service");
		response.sendRedirect(service + "/loginCallBack?" + SSO_TICKET + "=123456789");
	}
	
	/*@RequestMapping("/checkTicket")
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
	}*/

	@PostMapping("/validateToken")
	@ResponseBody
	public Map<String, String> validateToken(HttpServletRequest request){
		System.out.println("======>>server-validateToken");
		String ticket = request.getParameter(SSO_TICKET);
		Map<String, String> result = new HashMap<>();
		if("123456789".equals(ticket)){
			result.put("code", "200");
			return result;
		}
		result.put("code","400");
		return result;
	}
}
