package jdk7.io.netty.unit;

import org.junit.Test;

import java.util.Map;

import io.netty.handler.codec.http.DefaultHttpHeaders;

/** @author cctv 2018/3/9 */
public class DefaultHttpHeaderTest {
  /**
   * 类似LinkedHashMap，多一个双向链表来维持插入顺序
   * @throws Exception
   */
  @Test
  public void testDataStructure() throws Exception {
    DefaultHttpHeaders httpHeaders = new DefaultHttpHeaders();
    httpHeaders.add("a", "");
    httpHeaders.add("b", "");
    httpHeaders.add("c", "");
    for (Map.Entry<String, String> entry : httpHeaders) {
      System.out.println(entry);
    }
  }
}
