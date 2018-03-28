package com.netease.angel.interceptor;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 过期校验拦截器
 *
 * @author wangjinjie
 * @create 2017-10-28 13:25
 */
public class AdapterInterceptor1 extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o)
      throws Exception {
    System.out.println("Pre1");
    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView)
      throws Exception {
    System.out.println("Post1");
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object o, Exception e)
      throws Exception {
    System.out.println("After1");
  }

  @Override
  public void afterConcurrentHandlingStarted(
      HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    System.out.println("After Concurrent1");
  }
}
