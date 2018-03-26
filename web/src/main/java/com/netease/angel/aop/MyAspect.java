package com.netease.angel.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/** @author chenxiang 2018/3/26 */
@Component
@Aspect
public class MyAspect {
  /** 使用Pointcut定义切点 */
  // .. ：匹配方法定义中的任意数量的参数
  // * : 匹配任意名称的返回值
  @Pointcut("execution(* com.netease.angel.dao.UserDao.addUser(..))")
  private void myPointcut() {}

  /** 前置通知 绑定定义的PointCut */
  @Before(value = "myPointcut()")
  // JoinPoint包含了方法的信息，该参数可选
  public void before(JoinPoint joinPoint) {
    System.out.println("前置通知....");
  }

  /** 直接绑定待织入的函数 后置通知 returnVal,切点方法执行后的返回值 */
  @AfterReturning(value = "myPointcut()", returning = "returnVal")
  public void AfterReturning(Object returnVal) {
    System.out.println("后置通知...." + returnVal);
  }

  /**
   * 环绕通知
   *
   * @param joinPoint 可用于执行切点的类
   * @return
   * @throws Throwable
   */
  @Around(value = "myPointcut()")
  //  @Around("execution(* com.netease.angel.dao.UserDao.addUser(..))")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    System.out.println("环绕通知前....");
    Object obj = (Object) joinPoint.proceed();
    System.out.println("环绕通知后....");
    return obj;
  }

  /**
   * 抛出通知
   *
   * @param e
   */
  @AfterThrowing(value = "myPointcut()", throwing = "e")
  public void afterThrowable(Throwable e) {
    System.out.println("出现异常:msg=" + e.getMessage());
  }

  /** 类似于try finally中执行，所以在return之前执行 */
  @After(value = "execution(* com.netease.angel.dao.UserDao.addUser(..))")
  public void after() {
    System.out.println("最终通知....");
  }
}
