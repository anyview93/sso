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
        if(null != session){
			Object attribute = session.getAttribute("user");
			if(null != attribute){
				String s = attribute.toString();
				if("user".equals(s)) {
					return "index";
				}
			}
		}
        String user = request.getParameter("user");
		if("user".equals(user)){
            return "index";
        }
		response.sendRedirect("http://localhost:8080/cas-server/ssoServer?service=http://localhost:8081/cas-client1");
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
	public String loginCallBack(HttpServletRequest request,HttpServletResponse response) throws IOException {
		String ticket = request.getParameter("ticket");
		if(null != ticket && !"".equals(ticket)) {
			response.sendRedirect("http://localhost:8080/cas-server/checkTicket?service=http://localhost:8081/cas-client1&ticket=" + ticket);
			return null;
		}
		return "index";
	}
}
