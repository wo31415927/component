package jdk.container;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** @author cctv 2018/2/12 */
public class MyListTest {
  /**
   * 不设置cap，内部数组长度会缓慢增长，使用Arrays.copyOf()复制元素 设置cap后，cap内的元素不会再进行复制
   *
   * @throws Exception
   */
  @Test
  public void testArrayList() throws Exception {
    int cap = 16;
    List<Integer> list = new ArrayList<>(cap);
    //    List<String> list = new ArrayList<>();
    for (int i = 0; i < cap; i++) {
      list.add(i);
    }
    System.out.println(list.get(0));
  }

  /**
   * add很高效，get需要从头或者尾遍历以获取对应的元素
   * @throws Exception
   */
  @Test
  public void testLinkedList() throws Exception {
    int cap = 16;
    List<Integer> list = new LinkedList<>();
    //    List<String> list = new ArrayList<>();
    for (int i = 0; i < cap; i++) {
      list.add(i);
    }
    System.out.println(list.get(cap - 7));
  }
}
