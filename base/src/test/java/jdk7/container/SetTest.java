package jdk7.container;

import org.assertj.core.util.Sets;
import org.junit.Test;

import java.util.Set;

/** @author cctv 2018/2/28 */
public class SetTest {
  Set<String> set = Sets.newHashSet();

  {
    set.add("a");
    set.add("c");
    set.add("b");
  }

  @Test
  public void testRemove() throws Exception {
    Runtime.getRuntime().availableProcessors();
    for (String s : set) {
      set.remove(s);
    }
    System.out.println(set.size());
  }
}
