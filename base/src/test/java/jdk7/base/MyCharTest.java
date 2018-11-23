package jdk7.base;

import com.google.common.base.Splitter;

import org.junit.Test;

import java.nio.charset.Charset;
import java.util.StringTokenizer;

/** @author cctv 2018/2/3 */
public class MyCharTest {
  /** char的定义与int间的转换 */
  @Test
  public void testChar() throws Exception {
    char a = '\n'; //'国';
    String s = "" + a;
    byte[] b = s.getBytes();
    byte b1 = (byte) a;
    System.out.println(Charset.defaultCharset().name());
    char[] c1 = {
      5, 105, 205, 1005, 10005, 50005, 0x01, 0x1f6a, 0xffff, 0b0010, 00017, '\n', 'a', '国',
      '\u0001', '\u1f6a', '\uffff', '\377',
    };
    System.out.println(c1);
    System.out.println((int) '\\');
  }

  /** 16进制字符转换为10进制数 */
  @Test
  public void testChar1() throws Exception {
    System.out.println(Charset.defaultCharset().name());
    System.out.println(Character.digit('5', 16));
    System.out.println(Character.digit('d', 16));
    System.out.println(Character.digit('D', 16));
    System.out.println((char) Integer.parseInt("1f6a", 16));
  }

  /** \u56fd\u2f4c转换为对应的字符 */
  @Test
  public void decode() throws Exception {
    System.out.println(MyChar.decodeUnicode1("Hello\\u56fdMy\\u2f4cWorld!"));
    System.out.println(MyChar.decodeUnicode2("Hello\\u56fdMy\\u2f4cWorld!"));
  }

  /** 同样的字符在不同字符集下占用不同长度的字节 */
  @Test
  public void testChar2() throws Exception {
    String[] sArr = {"a","1","国"};
    for(String s : sArr) {
        //3字节
        byte[] b = s.getBytes(Charset.forName("utf8"));
        //4字节
        byte[] b0 = s.getBytes(Charset.forName("utf16"));
        //2字节
        byte[] b1 = s.getBytes(Charset.forName("gbk"));
        byte[] b2 = s.getBytes(Charset.forName("iso8859-1"));
        //4字节，按网上说应该所有的unicode都是2字节来着
        byte[] b3 = s.getBytes(Charset.forName("unicode"));
        System.out.println("测试字符串：" + s);
        System.out.println("utf8:" + b.length);
        System.out.println("utf16:" + b0.length);
        System.out.println("gbk:" + b1.length);
        System.out.println("iso8859-1:" + b2.length);
        System.out.println("unicode:" + b3.length);
        System.out.println("---");
    }
  }

  /**
   * hasMore必须和next联用
   *
   * @throws Exception
   */
  @Test
  public void testStringToken() throws Exception {
    StringTokenizer tokenizer = new StringTokenizer("Hello|World|End", "|");
    while (tokenizer.hasMoreElements()) {
      System.out.println(tokenizer.nextElement());
    }
  }

  /**
   * next()中隐式调用hasNext()
   *
   * @throws Exception
   */
  @Test
  public void testStringSplitter() throws Exception {
    String str = "Hello|World|End";
    String delimiter = "|";
    Splitter splitter = Splitter.on(delimiter);
    System.out.println(splitter.splitToList(str));
  }
}
