package com.netease.angel.pojo;

/** @author chenxiang 2018/3/28 */
public class Student {
  private String code;
  private String name;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Student{" + "code='" + code + '\'' + ", name='" + name + '\'' + '}';
  }
}
