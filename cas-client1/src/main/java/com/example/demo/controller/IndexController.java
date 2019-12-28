/**
 *
 */
package com.example.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * IndexController.java
 *
 *
 * @author shizhiguo
 * @date 2018年11月26日
 */
@CrossOrigin
@Controller
public class IndexController {
    public static final String SSO_USER = "sso.user";
    public static final String CLIENT_USER = "client.user";
    public static final String SSO_TICKET = "sso.ticket";

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("======>>client-login");
        HttpSession session = request.getSession();
        if (null != session) {
            Object ssoUser = session.getAttribute(SSO_USER);
            if (null != ssoUser) {
                session.setAttribute(CLIENT_USER, ssoUser);
            }
        }
        return "index";
    }

    @GetMapping("/logout")
    @ResponseBody
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("stat");
        return "登出成功";
    }

    @GetMapping("/permission")
    public String permission() {
        return "index";
    }
}
