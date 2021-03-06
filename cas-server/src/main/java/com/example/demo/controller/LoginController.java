/**
 *
 */
package com.example.demo.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.demo.entity.SystemInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Subject;
import com.example.demo.entity.User;
import sun.net.www.http.HttpClient;

/**
 * LoginController.java
 *
 *
 * @author shizhiguo
 * @date 2018年11月26日
 */
@CrossOrigin
@Controller
public class LoginController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CacheManager cacheManager;

    public static final String SSO_USER = "sso.user";
    public static final String SSO_TICKET = "ticket";
    public static final String SSO_SERVICE = "service";
    public static final String SSO_TGC = "CAS_TGC";
    private static final String LOGOUT_REQUEST = "logout.request";
    private static final String LOGOUT_URL = "logoutUrl";
    private static final String SESSIONID = "sessionId";
    private static final Map<String, String> users = new HashMap<>();
    private static final Map<String , List<SystemInfo>> subSystems = new ConcurrentHashMap<>();
    private static final Map<String, String> sessions = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> sts = new ConcurrentHashMap<>();
    private static final Map<String, Subject> maps = new ConcurrentHashMap<>();


    static {
        users.put("user", "123");
        users.put("test", "123");
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, RedirectAttributes attr) throws IOException {
        System.out.println("======>>server-toLogin");
        String service = request.getParameter(SSO_SERVICE);
        attr.addAttribute(SSO_SERVICE, service);
        Cookie[] cookies = request.getCookies();
        String tgt = "";
        if (null != cookies && cookies.length > 0) {
            for (int i = 0; i < cookies.length; i++) {
                if (SSO_TGC.equals(cookies[i].getName())) {
                    tgt = cookies[i].getValue();
                    break;
                }
            }
        }
        if(StringUtils.isNotBlank(tgt)){
            Subject subject = maps.get(tgt);
            if(null != subject){
                String st = getTicket(tgt);
                if(subject.getTickets() == null){
                    subject.setTickets(new HashSet<>());
                }
                subject.getTickets().add(st);
                Map<String, String> param = new HashMap<>();
                param.put(SSO_TICKET,st);
                redirectClient(request,response,service,param);
                return null;
            }
        }
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response,@RequestBody User user) throws IOException {
        System.out.println("======>>server-login");
        String service = request.getParameter(SSO_SERVICE);
        if (users.containsKey(user.getName()) && users.get(user.getName()).equals(user.getPassword()) && !StringUtils.isEmpty(service)) {
            HttpSession session = request.getSession();
            String tgt = setCookie(response);
            String st = getTicket(tgt);
            Subject subject = Subject.builder().sessionId(tgt).user(user).build();
            if(null == subject.getTickets()){
                subject.setTickets(new HashSet<>());
            }
            subject.getTickets().add(st);
            maps.put(tgt,subject);
            session.setAttribute(SSO_USER, subject);
            Map<String, String> param = new HashMap<>();
            param.put(SSO_TICKET,st);
//            redirectClient(request, response, service, param);
            return redirectUrl(service,param);
        }
//        response.sendRedirect("/cas-server/login?service=" + service);
        return "/cas-server/login?service=" + service;
    }

    private void redirectClient(HttpServletRequest request, HttpServletResponse response, String service, Map<String,String> param) throws IOException {
//        final String tgt = setCookie(response);
//        final String st = getTicket(tgt);
//		addSystem(service, tgt);
//        HttpSession session = request.getSession();
//        sessions.put(tgt, session.getId());
//        Cache cache = cacheManager.getCache(CacheEnum.SESSIONS.name());
//        cache.putIfAbsent(session.getId(), session);
        StringBuilder builder = new StringBuilder().append("?");
        if(null != param && !param.isEmpty()){
            param.entrySet()
                    .forEach(e -> builder.append(e.getKey()).append("=").append(e.getValue()).append("&"));
        }
        String paramString = builder.deleteCharAt(builder.length() - 1).toString();
        response.sendRedirect(service + paramString);
    }

    private String redirectUrl(String service, Map<String,String> param) throws IOException {
        StringBuilder builder = new StringBuilder().append(service).append("?");
        if(null != param && !param.isEmpty()){
            param.entrySet()
                    .forEach(e -> builder.append(e.getKey()).append("=").append(e.getValue()).append("&"));
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    private String setCookie(HttpServletResponse response) {
        final String tgt = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(SSO_TGC, tgt);
        response.addCookie(cookie);
        return tgt;
    }

    private void addSystem(String service, String tgt, String ticket) {
        if (null == subSystems.get(tgt)) {
            subSystems.put(tgt, new ArrayList<>());
        }
        subSystems.get(tgt).add(new SystemInfo(ticket,service));
    }

    private final String getTicket(String tgt) {
        final String st = UUID.randomUUID().toString();
        /*Subject subject = maps.get(tgt);
        List<String> tickets = subject.getTickets();*/
        /*if(null == tickets){
            tickets = new ArrayList<>();
        }*/
//        tickets.add(st);
        return st;
    }

    @PostMapping("/validateTicket")
    @ResponseBody
    public User validateTicket(@RequestBody Map<String, String> param) {
        System.out.println("======>>server-validateToken");
        String ticket = param.get(SSO_TICKET);
        String service = param.get(SSO_SERVICE);

        for (Map.Entry<String, Subject> entry : maps.entrySet()) {
            Subject subject = entry.getValue();
            if(subject.getTickets().contains(ticket)){
                addSystem(service,subject.getSessionId(),ticket);
                if(subject.getLogoutUrls() == null){
                    subject.setLogoutUrls(new HashSet<>());
                }
                subject.getLogoutUrls().add(service);
                return subject.getUser();
            }
        }
        return null;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        String tgt = "";
        if (null != cookies && cookies.length > 0) {
            for (int i = 0; i < cookies.length; i++) {
                if (SSO_TGC.equals(cookies[i].getName())) {
                    tgt = cookies[i].getValue();
                    cookies[i].setMaxAge(0);
                    break;
                }
            }
        }
        if(subSystems.containsKey(tgt)){
            List<SystemInfo> systemInfos = subSystems.get(tgt);
            subSystems.remove(tgt);
            for (SystemInfo sys : systemInfos) {
                String url = sys.getSystemUrl() + "/logout?" + SSO_TICKET + "=" + sys.getTicket();
                restTemplate.delete(url);
            }
        }
        maps.remove(tgt);
        return "logout";
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update("string".getBytes("UTF-8"));
        String st = byteArrayToHex(md5.digest());
        System.out.println(st);
    }

    private static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））

        char[] resultCharArray = new char[byteArray.length * 2];

        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];

        }

        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }
}
