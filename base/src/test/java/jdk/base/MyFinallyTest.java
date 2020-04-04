package jdk.base;

import org.junit.Test;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/** @author chenxiang 2019/6/11/011 */
@Slf4j
public class MyFinallyTest {
  /** 测试finally是否会在throw之前执行 */
  @Test
  public void testFinally() {
    try {
      throw (RuntimeException) new RuntimeException("").initCause(new IOException(""));
    } finally {
      log.info("print finally.");
    }
  }
}
