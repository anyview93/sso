/**
 * 
 */
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * IndexController.java
 * 
 *
 * @author shizhiguo
 * @date 2018年11月26日
 */
@Controller
public class IndexController {
	public static final String SSO_USER = "sso.user";
	public static final String CLIENT_USER = "client.user";
	public static final String SSO_TICKET = "sso.ticket";
	@RequestMapping("/index")
	public String index() {
		return "index";
	}
	@RequestMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("======>>client-login");
		HttpSession session = request.getSession();
        if(null != session){
			Object ssoUser = session.getAttribute(SSO_USER);
			if(null != ssoUser){
				session.setAttribute(CLIENT_USER,ssoUser);
			}
		}
		return "index";
	}
	@RequestMapping("/logout")
	@ResponseBody
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute("stat");
		return "登出成功";
	}

	@RequestMapping("/permission")
	public String permission(){
		return "index";
	}
}
