package com.netease.angel.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/** @author chenxiang 2018/3/26 */
@Component
public class MyAspectXML {

  public void before() {
    System.out.println("MyAspectXML====前置通知");
  }

  public void afterReturn(Object returnVal) {
    System.out.println("后置通知-->返回值:" + returnVal);
  }

  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    System.out.println("MyAspectXML=====环绕通知前");
    Object object = joinPoint.proceed();
    System.out.println("MyAspectXML=====环绕通知后");
    return object;
  }

  public void afterThrowing(Throwable throwable) {
    System.out.println("MyAspectXML======异常通知:" + throwable.getMessage());
  }

  public void after() {
    System.out.println("MyAspectXML=====最终通知..来了");
  }
}
