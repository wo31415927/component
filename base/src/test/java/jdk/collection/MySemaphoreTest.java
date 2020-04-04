package jdk.collection;

import org.junit.Test;

import java.util.concurrent.Semaphore;

/**
 * @author chenxiang 2019/6/14/014
 */
public class MySemaphoreTest {

  @Test
  public void testSemaphore() {
      Semaphore semaphore = new Semaphore(3,true);
  }
}