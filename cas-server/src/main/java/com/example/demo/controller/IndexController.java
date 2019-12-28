/**
 *
 */
package com.example.demo.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/index")
    public String index() {
        System.out.println("=========>>>首页");
        return "index";
    }

    @GetMapping("/user/{uid}")
    @Cacheable(value = "users", key = "#uid + ':getUser'")
    @ResponseBody
    public String getUser(@PathVariable("uid") String uid) {
        System.out.println("=========>>>" + uid);
        return uid;
    }

}
