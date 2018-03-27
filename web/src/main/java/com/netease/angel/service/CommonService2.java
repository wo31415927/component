package com.netease.angel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** @author chenxiang 2018/3/27 */
@Service
public class CommonService2 implements ICommonService {

  @Override
  public void insert() {}

//  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  @Transactional(readOnly = true)
  @Override
  public void delete() {
    System.out.println("CommonService2 delete");
  }
}
