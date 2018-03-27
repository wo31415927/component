package com.netease.angel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/** @author chenxiang 2018/3/24 */
@Service
public class CommonService1 implements ICommonService {
  @Resource(name = "commonService2")
  protected ICommonService commonService2;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void insert() {
    System.out.println("CommonService1 tx start");
    commonService2.delete();
    System.out.println("CommonService1 tx end");
  }

  @Override
  public void delete() {}
}
