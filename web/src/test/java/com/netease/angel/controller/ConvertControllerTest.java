package com.netease.angel.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/** @author chenxiang 2018/3/28 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
  locations = {"classpath:/context.xml", "classpath:/springMVC-servlet.xml"}
)
public class ConvertControllerTest {
  @Test
  public void jsonTest() {
    System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
  }
}
