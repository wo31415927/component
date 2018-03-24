package com.netease.angel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** @author chenxiang 2018/3/24 */
@Service
public class CommonService1 implements ICommonService {
  @Transactional
  @Override
  public void insert() {
    System.out.println("CommonService1");
  }
}
