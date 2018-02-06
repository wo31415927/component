package jdk.base;

import com.google.common.truth.Truth;

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    List<String> pList =
        Lists.newArrayList("\u005c\u005c\u005c\u005c", "\\u005c", "\u005c\u0022", "\\u0022");
    System.out.println(str.replaceAll(pList.get(3), ""));
  }

  //

  @Test
  public void testBasePattern() throws Exception {
    String s1 = "u0022";
    String r1 = "(u[0-9a-fA-Z]{4})";
    Matcher m1 = Pattern.compile(r1).matcher(s1);
    if (m1.find()) {
      System.out.println(m1.group(1));
      r1 = m1.replaceAll("\\\\$1");
    }
    System.out.println(r1);
  }

  public String filter(String str) {
    String regex = "_(u[0-9a-fA-Z]{4})";
    Matcher m1 = Pattern.compile(regex).matcher(str);
    if (m1.find()) {
      return m1.replaceAll("\\\\$1");
    }
    return str;
  }

  /**
   * matches()是完全匹配
   * find()是部分匹配
   * 1)所有转义字符一律写成_uXXXX,转化成\uXXXX,作为普通字符由replace替换
   * 2)会与json冲突的字符，如"\等，统一写成\uXXXX
   * @throws Exception
   */
  @Test
  public void testEasyPattern() throws Exception {
    String r1 = filter("[a-z_u000a_u0022]u007b2u0027u007d");
    Matcher m1 = Pattern.compile(r1).matcher("+\"\n");
    if (m1.find()) {
      System.out.println(true);
      return;
    }
  }

  @Test
  public void testComplexPattern() throws Exception {
    String r1 = filter("[a-z_u0022]+");
    Matcher m1 = Pattern.compile(r1).matcher("\"");
    if (m1.matches()) {
      System.out.println(true);
      return;
    }
    String regex =
        "([0-9a-zA-Z_]+)@[\\u005c\\u007B\\u007D\\u002B\\u0022\\u002D\\u002C\\u002eA-Za-z_]+@([0-9a-zA-Z]+)\\.([a-zA-Z]+)";
    System.out.println(regex);
    String s = "wo31415927@\\{}\"_+A-,.@163.com";
    Matcher matcher = Pattern.compile(regex).matcher(s);
    if (matcher.find()) {
      for (int i = 0; i <= matcher.groupCount(); i++) {
        System.out.println("Group" + i + ":" + matcher.group(i));
      }
    }
    System.out.println("Replace:" + s.replaceAll(regex, ""));
  }
}
