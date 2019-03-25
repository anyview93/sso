/**
 * 
 */
package com.example.demo.controller;

import com.example.demo.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.AlgorithmConstraints;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
	private static Map<String, String> subSystems = new ConcurrentHashMap<>();
	private static Map<String, String> sessions = new ConcurrentHashMap<>();
	static {
		user.put("user", "123");
		user.put("test","123");
	}

	@GetMapping("/login")
	public String login(HttpServletRequest request, RedirectAttributes attr){
		System.out.println("======>>server-toLogin");
		String service = request.getParameter("service");
		attr.addAttribute("service", service);
		return "login";
	}

	@PostMapping("/login")
	public void login(HttpServletRequest request,HttpServletResponse response, String name, String password) throws IOException, NoSuchAlgorithmException {
		System.out.println("======>>server-login");
		String service = request.getParameter("service");
		if(user.containsKey(name) && user.get(name).equals(password) && !StringUtils.isEmpty(service)) {
			HttpSession session = request.getSession();
			final String TGT = UUID.randomUUID().toString();
			sessions.put(session.getId(),TGT);
			User user = new User(name, name);
			session.setAttribute(SSO_USER, user);
			final String ST = MessageDigest.getInstance("MD5").digest(TGT.getBytes("UTF-8")).toString();
			response.sendRedirect(service + "？" + SSO_TICKET + "=" + ST);
			return;
		}
		response.sendRedirect("/cas-server/login?service=" + service);
	}
	
	@RequestMapping("/ssoServer")
	public void ssoServer(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("======>>server-ssoServer");
		HttpSession session = request.getSession();
		String service = request.getParameter("service");
		Object ssoUser = session.getAttribute(SSO_USER);
		if(null == ssoUser){
			response.sendRedirect("/cas-server/login?service=" + service);
			return;
		}
		response.sendRedirect(service + "/loginCallBack?" + SSO_TICKET + "=123456789");
	}

	@PostMapping("/validateTicket")
	@ResponseBody
	public User validateTicket(@RequestBody Map<String, String> param){
		System.out.println("======>>server-validateToken");
		String ticket = param.get(SSO_TICKET);
		if("123456789".equals(ticket)){
			User user = new User();
			user.setId("001");
			user.setName("user");
			return user;
		}
		return null;
	}
}
