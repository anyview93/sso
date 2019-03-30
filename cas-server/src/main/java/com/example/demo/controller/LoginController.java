/**
 * 
 */
package com.example.demo.controller;

import com.example.demo.common.CacheEnum;
import com.example.demo.entity.Subject;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private CacheManager cacheManager;

	public static final String SSO_USER = "sso.user";
	public static final String SSO_TICKET = "ticket";
	public static final String TGC = "CASTGC";
	private static final String LOGOUT_REQUEST = "logout.request";
	private static final String LOGOUT_URL = "logoutUrl";
	private static final String SESSIONID = "sessionId";
	private static final Map<String, String> user = new HashMap<>();
	private static final Map<String, Set<String>> subSystems = new ConcurrentHashMap<>();
	private static final Map<String, String> sessions = new ConcurrentHashMap<>();
	private static final Map<String, Set<String>> sts = new ConcurrentHashMap<>();
	private static final Map<String,List<Subject>> maps = new ConcurrentHashMap<>();


	static {
		user.put("user", "123");
		user.put("test","123");
	}

	@GetMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response, RedirectAttributes attr){
		System.out.println("======>>server-toLogin");
		String service = request.getParameter("service");
		attr.addAttribute("service", service);
		Cookie[] cookies = request.getCookies();
		String tgt = "";
		if(null != cookies && cookies.length > 0){
			for (int i = 0; i < cookies.length; i++) {
				if (TGC.equals(cookies[i].getName())){
					tgt = cookies[i].getValue();
					break;
				}
			}
		}
//		Set<Subject> subjects = maps.get(tgt);

//		Cache cache = cacheManager.getCache(CacheEnum.SESSIONS.name());
//		HttpSession session = cache.get(tgt,HttpSession.class);
		HttpSession session = request.getSession();
		final User user = (session != null) ? (User)session.getAttribute(SSO_USER) : null;
		if(null != user){
			try {
				final String st = getTicket(tgt);
				addSystem(service, tgt);
				response.sendRedirect(service + "？" + SSO_TICKET + "=" + st);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		return "login";
	}

	@PostMapping("/login")
	public void login(HttpServletRequest request,HttpServletResponse response, String name, String password) throws IOException{
		System.out.println("======>>server-login");
		String service = request.getParameter("service");
		if(user.containsKey(name) && user.get(name).equals(password) && !StringUtils.isEmpty(service)) {
			HttpSession session = request.getSession();
			User user = new User(name, name);
			session.setAttribute(SSO_USER, user);
			redirectClient(request, response, service);
			return;
		}
		response.sendRedirect("/cas-server/login?service=" + service);
	}

	private void redirectClient(HttpServletRequest request, HttpServletResponse response, String service) throws IOException {
		final String tgt = setCookie(response);
		final String st = getTicket(tgt);
//		addSystem(service, tgt);
		HttpSession session = request.getSession();
		sessions.put(tgt,session.getId());
		Cache cache = cacheManager.getCache(CacheEnum.SESSIONS.name());
		cache.putIfAbsent(session.getId(),session);
		response.sendRedirect(service + "？" + SSO_TICKET + "=" + st);
	}

	private String setCookie(HttpServletResponse response) {
		final String tgt = UUID.randomUUID().toString();
		Cookie cookie = new Cookie(TGC, tgt);
		response.addCookie(cookie);
		return tgt;
	}

	private void addSystem(String service, String tgt) {
		Set<String> subSystemSet = subSystems.get(tgt);
		if(null == subSystemSet){
			subSystemSet = new HashSet<>();
			subSystems.put(tgt,subSystemSet);
		}
		subSystemSet.add(service);
		List<Subject> subjects = maps.get(tgt);
		if(null == subjects){
			subjects = new ArrayList<>();
			maps.put(tgt, subjects);
		}
	}

	private final String getTicket(String tgt){
		final String st = UUID.randomUUID().toString();
		List<Subject> subjects = maps.get(tgt);
		if(null == subjects){
			subjects = new ArrayList<>();
			maps.put(tgt, subjects);
		}
		Subject subject = new Subject.Builder()
				.setTicket(st)
				.build();
		subjects.add(subject);
		/*Set<String> stSet = sts.get(tgt);
		if(null == stSet){
			stSet = new HashSet<>();
			sts.put(tgt,stSet);
		}
		stSet.add(st);*/
		return st;
	}

	@PostMapping("/validateTicket")
	@ResponseBody
	public User validateTicket(@RequestBody Map<String, String> param){
		System.out.println("======>>server-validateToken");
		String ticket = param.get(SSO_TICKET);
		String logoutUrl = param.get(LOGOUT_URL);
		String sessionId = param.get(SESSIONID);
		for (Map.Entry<String, Set<String>> entry: sts.entrySet()){
			String tgt = entry.getKey();
			if(entry.getValue().contains(ticket)){
				Subject subject = new Subject.Builder()
						.setSessionId(sessionId)
						.setLogoutUrl(logoutUrl)
						.setTicket(ticket)
						.build();
				List<Subject> subjects = maps.get(tgt);
				if(null == subjects){
					subjects = new ArrayList<>();
					maps.put(entry.getKey(),subjects);
				}
				subjects.add(subject);
				Cache cache = cacheManager.getCache(CacheEnum.SESSIONS.name());
				HttpSession session = cache.get(sessionId, HttpSession.class);
				Object obj = session.getAttribute(SSO_USER);
				User user = (obj instanceof User) ? (User)obj : null;
				return user;
			}
		}
		return null;
	}

	@RequestMapping("/logout")
	public void logout(HttpServletRequest request){
		Cookie[] cookies = request.getCookies();
		String tgt = "";
		if(null != cookies && cookies.length > 0){
			for (int i = 0; i < cookies.length; i++) {
				if (TGC.equals(cookies[i].getName())){
					tgt = cookies[i].getValue();
					break;
				}
			}
		}
		Set<String> subSystemSet = subSystems.get(tgt);
		for (String url: subSystemSet){
			HashMap<String, Set<String>> param = new HashMap<>();
			Set<String> stSet = sts.get(tgt);
			param.put(LOGOUT_REQUEST,stSet);
//			restTemplate.postForObject(url + "/logout",param , null);
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update("string".getBytes("UTF-8"));
		String st = byteArrayToHex(md5.digest());
		System.out.println(st);
	}

	private static String byteArrayToHex(byte[] byteArray){
		// 首先初始化一个字符数组，用来存放每个16进制字符
		char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };

		// new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））

		char[] resultCharArray =new char[byteArray.length * 2];

		// 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b& 0xf];

		}

		// 字符数组组合成字符串返回
		return new String(resultCharArray);
	}
}
