import org.junit.Test;

/** @author chenxiang 2019/1/30 */
public class SkipListTest {
  @Test
  public void testPut() {
    SkipList<String> list = new SkipList<>();
    System.out.println(list);
    list.put(2, "yan");
    list.put(1, "co");
    list.put(3, "feng");
    // 测试同一个key
    list.put(1, "cao");
    list.put(4, "曹");
    list.put(6, "丰");
    list.put(5, "艳");
    System.out.println(list);
  }
}
