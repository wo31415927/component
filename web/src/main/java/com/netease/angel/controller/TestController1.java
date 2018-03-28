package com.netease.angel.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** @author chenxiang 2018/3/27 */
public class TestController1 extends AbstractController {
  @Override
  protected ModelAndView handleRequestInternal(
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    // 获取 Controller 的名称，即地址
    System.out.println(request.getRequestURI());
    //触发异常，使其走SimpleMappingExceptionResolver
    int i = 1/0;
    // 逻辑名
    return new ModelAndView("index");
  }
}
