package com.netease.angel.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/** @author chenxiang 2018/3/24 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
  locations = {"classpath*:/context.xml"
    //    "file:src/main/webapp/WEB-INF/springMVC-servlet.xml"
  }
)
public class CommonService1Test {
  @Resource(name = "commonService1")
  protected ICommonService commonService1;

  @Test
  public void insert() {
    commonService1.insert();
  }
}
