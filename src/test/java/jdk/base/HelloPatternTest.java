package jdk.base;

import com.google.common.truth.Truth;

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

/** @author zeyu 2017/11/28 */
public class HelloPatternTest {
  @Test
  public void testPattern() throws Exception {
    Truth.assertThat("select * from a limit 100".matches(".*limit [\\d]+$")).isTrue();
    Truth.assertThat("select * from a where id < 1".matches(".*limit [\\d]+$")).isFalse();
  }

  @Test
  public void testSpecChar() throws Exception {
    String str = "He\\llo\"";
    //\u005c,一个，idea直接报编译错误
    //两个，pattern报compile错误
//    List<String> pList = Lists.newArrayList();
    List<String> pList = Lists.newArrayList("\u005c\u005c\u005c\u005c","\\u005c","\u005c\u0022","\\u0022");
    System.out.println(str.replaceAll(pList.get(3), ""));
  }
}
