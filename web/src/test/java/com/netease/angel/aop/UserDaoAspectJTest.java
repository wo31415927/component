package com.netease.angel.aop;

import com.netease.angel.dao.UserDao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/** @author chenxiang 2018/3/26 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context.xml")
public class UserDaoAspectJTest {
  @Autowired UserDao userDao;

  @Test
  public void aspectJTest() {
    userDao.addUser();
    //    userDao.deleteUser();
  }
}
