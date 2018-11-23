package jdk7.container;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/** @author cctv 2018/1/15 */
public class CacheTest {
  @Test
  public void testCache() throws Exception {
    Cache<Integer, String> cache = new Cache<Integer, String>();
    cache.put(1, "aaaa", 3, TimeUnit.SECONDS);

    Thread.sleep(1000 * 2);
    {
      String str = cache.get(1);
      System.out.println(str);
    }

    Thread.sleep(1000 * 2);
    {
      String str = cache.get(1);
      System.out.println(str);
    }
  }
}
