package tmp;

import org.junit.Test;

/** @author chenxiang 2019/6/24/024 */
public class TmpBaseTest {
  @Test
  public void testFinally() {
    System.out.println(testReturn());
  }

  public String testReturn() {
    try {
      return "normal";
    } finally {
      return "finally";
    }
  }
}
