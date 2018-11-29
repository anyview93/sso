/**
 * 
 */
package com.example.demo.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * IndexController.java
 * 
 *
 * @author shizhiguo
 * @date 2018年11月26日
 */
@Controller
public class IndexController {
	
	@RequestMapping("")
	public String index() {
		return "index";
	}
	@RequestMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		if("client1".equals(session.getAttribute("user"))) {
			return "index";
		}
		response.sendRedirect("http://localhost:8080/cas-server/login?server=http://localhost:8081/cas-client1");
		return null;
	}
	@RequestMapping("/logout")
	@ResponseBody
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute("stat");
		return "登出成功";
	}
	
	@RequestMapping("/loginCallBack")
	public String loginCallBack(HttpServletRequest request,HttpServletResponse response) {
		String ticket = request.getParameter("ticket");
		if(null != ticket && !"".equals(ticket)) {
			request.getSession().setAttribute("user", "client1");
			response.addCookie(new Cookie("ticket", ticket));
		}
		return "index";
	}
}
