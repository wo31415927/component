package com.netease.angel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/** @author chenxiang 2018/3/27 */
//@RestController
@Controller
//@RequestMapping("/api")
public class TestController2 {
  @RequestMapping(value = "/index2", method = RequestMethod.GET)
  public String getAppInfo(HttpServletRequest request) {
    System.out.println(request.getRequestURI());
    return "index";
  }
}
