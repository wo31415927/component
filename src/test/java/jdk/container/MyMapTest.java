package jdk.container;

import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** @author cctv 2018/1/5 */
public class MyMapTest {
  Map<String, String> map;

  @Test
  public void testHash() throws Exception {
    System.out.println(System.identityHashCode(null));
  }

  @Test
  public void testHashMap() throws Exception {
    map = new HashMap<String, String>();
    map.put("apple", "苹果");
    map.put("watermelon", "西瓜");
    map.put("banana", "香蕉");
    map.put("peach", "桃子");

    Iterator iter = map.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      System.out.println(entry.getKey() + "=" + entry.getValue());
    }
  }

  @Test
  public void testLinkedHashMap() throws Exception {
    map = new LinkedHashMap<String, String>();
    map.put("apple", "苹果");
    map.put("watermelon", "西瓜");
    map.put("banana", "香蕉");
    map.put("peach", "桃子");

    Iterator iter = map.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      System.out.println(entry.getKey() + "=" + entry.getValue());
    }
  }
}
