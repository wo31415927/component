package com.netease.angel.pojo;

import lombok.Getter;
import lombok.Setter;

/** @author chenxiang 2018/3/28 */
@Getter
@Setter
public class Student {
  private String code;
  private String name;

  @Override
  public String toString() {
    return "Student{" + "code='" + code + '\'' + ", name='" + name + '\'' + '}';
  }
}
