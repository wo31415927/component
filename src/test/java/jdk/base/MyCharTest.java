package jdk.base;

import org.junit.Test;

import java.nio.charset.Charset;

/** @author cctv 2018/2/3 */
public class MyCharTest {
  /**
   * char的定义与int间的转换
   * @throws Exception
   */
  @Test
  public void testChar() throws Exception {
    System.out.println(Charset.defaultCharset().name());
    char[] c1 = {
            5, 105, 205, 1005, 10005, 50005, 0x01, 0x1f6a, 0xffff, 0b0010, 00017, '\n', 'a', '国',
            '\u0001', '\u1f6a', '\uffff', '\377',
    };
    char c = 97;
    int a = 'a';
    int b = '国';
    System.out.println(c1);
    System.out.println((int)'\\');
  }

  /**
   * 16进制字符转换为10进制数
   * @throws Exception
   */
  @Test
  public void testChar1() throws Exception {
    System.out.println(Charset.defaultCharset().name());
    System.out.println(Character.digit('5', 16));
    System.out.println(Character.digit('d', 16));
    System.out.println(Character.digit('D', 16));
    System.out.println((char) Integer.parseInt("1f6a", 16));
  }
  /**
   * \u56fd\u2f4c转换为对应的字符
   * @throws Exception
   */
  @Test
  public void decode() throws Exception {
    System.out.println(MyChar.decodeUnicode1("Hello\\u56fdMy\\u2f4cWorld!"));
    System.out.println(MyChar.decodeUnicode2("Hello\\u56fdMy\\u2f4cWorld!"));
  }

  /**
   * 同样的字符在不同字符集下占用不同长度的字节
   * @throws Exception
   */
  @Test
  public void testChar2() throws Exception {
    String s = "国";
    //3字节
    byte[] b = s.getBytes(Charset.forName("utf8"));
    //2字节
    byte[] b1 = s.getBytes(Charset.forName("gbk"));
    //?
    byte[] b2 = s.getBytes(Charset.forName("iso8859-1"));
    //4字节，按网上说应该所有的unicode都是2字节来着
    byte[] b3 = s.getBytes(Charset.forName("unicode"));

    System.out.println(new String(b, Charset.forName("utf8")));
    System.out.println(new String(b1, Charset.forName("gbk")));
    System.out.println(new String(b2, Charset.forName("iso8859-1")));
    System.out.println(new String(b3, Charset.forName("unicode")));
  }
}
