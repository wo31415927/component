package jvm;

import com.google.common.truth.Truth;

import org.junit.Test;

import java.util.Random;
/** @author cctv 2018/2/22 */
/**
 * 1) 直接使用双引号声明出来的String对象会直接存储在常量池中。 2) 如果不是用双引号声明的String对象，可以使用String提供的intern方法。 intern
 * 方法会从字符串常量池中查询当前字符串是否存在，若不存在就会将当前字符串放入常量池中
 *
 * @throws Exception
 */
public class StringPoolTest {
  static final int MAX = 10737410;
  static final String[] arr = new String[MAX];
  /**
   * 修改maven compile source\target均为1.7
   * jdk17下string pool的表现
   * 要设置jdk为1.7
   * 要设置Build no check errors
   *
   * @throws Exception
   */
  @Test
  public void testStringInternJdk17() throws Exception {
    //无论是jdk7还是8，new String()和string pool都不在一个区域
    //string pool
    String a = "text";
    //jvm heap
    String b = new String("text");
    Truth.assertThat(a == b).isFalse();
    Truth.assertThat(b != b.intern()).isTrue();
    Truth.assertThat(a == b.intern()).isTrue();

    //声明"1"时已将1放入pool,s指向heap
    String s = new String("1");
    //pool中已经存在"1"，所以不会将s的引用放入pool
    s.intern();
    //pool中的引用
    String s2 = "1";
    //所以不等
    Truth.assertThat(s == s2).isFalse();

    //s3指向heap
    String s3 = new String("1") + new String("1");
    //17下判断pool中无11，所以将s3在heap中的引用放入pool
    s3.intern();
    //获取pool中的引用
    String s4 = "11";
    //所以相等
    Truth.assertThat(s3 == s4).isTrue();

    s = new String("2");
    s2 = "2";
    s.intern();
    Truth.assertThat(s == s2).isFalse();

    s3 = new String("2") + new String("2");
    s4 = "22";
    //pool中已存在，所以不会放入heap中的引用，所以不等
    s3.intern();
    Truth.assertThat(s3 == s4).isFalse();
  }

  /**
   * jdk18下string pool的表现
   * 要设置jdk为1.8
   * 要设置Build no check errors
   * @throws Exception
   */
  @Test
  public void testStringInternJdk18() throws Exception {
    //string pool
    String a = "text";
    //jvm heap
    String b = new String("text");
    Truth.assertThat(a == b).isFalse();
    Truth.assertThat(b != b.intern()).isTrue();
    Truth.assertThat(a == b.intern()).isTrue();

    //声明"1"时已将1放入pool,s指向heap
    String s = new String("1");
    //pool中已经存在"1"，所以不会将s的引用放入pool
    s.intern();
    //pool中的引用
    String s2 = "1";
    //所以不等
    Truth.assertThat(s == s2).isFalse();

    String s3 = new String("1") + new String("1");
    //jdk18可能不再放入s3在heap中的引用，所以又不相等了
    s3.intern();
    String s4 = "11";
    Truth.assertThat(s3 == s4).isFalse();

    s = new String("2");
    s2 = "2";
    s.intern();
    Truth.assertThat(s == s2).isFalse();

    s3 = new String("2") + new String("2");
    s4 = "22";
    s3.intern();
    Truth.assertThat(s3 == s4).isFalse();
  }

  /**
   * 注意要设置gc参数-Xmx2g -Xms2g -Xmn1500M
   * 要设置Build no check errors
   * @throws Exception
   */
  @Test
  public void testPrefIntern() throws Exception {
    Integer[] sample = new Integer[10];
    Random random = new Random(1000);
    for (int i = 0; i < sample.length; i++) {
      sample[i] = random.nextInt();
    }
    // 记录程序开始时间
    long t = System.currentTimeMillis();
    for (int i = 0; i < arr.length; i++) {
      //没有intern()好用大量的空间但更快的时间
      arr[i] = new String(String.valueOf(sample[i % sample.length])); //.intern();
      //有intern()使用很少的空间，但由于去pool中hash查找，会多耗一点时间，1000w查找1s左右
      //arr[i] = new String(String.valueOf(sample[i % sample.length])).intern();
    }
    System.out.println((System.currentTimeMillis() - t) + "ms");
  }

  /**
   * 修改maven compile source\target均为1.7
   * 观察String pool在jdk17下占用的内存区域
   * 设置较小的Xmx128m
   * 1. string pool在java heap中，young与old都会分布
   * 2. string pool会进行gc，string数量会增加和减少
   * @throws Exception
   */
  @Test
  public void testStringPoolMemoryJdk17() throws Exception {
    int i = 1;
    while (true) {
      String.valueOf(i++).intern();
    }
  }

  /**
   * 修改maven compile source\target均为1.7
   * 观察String pool在jdk18下占用的内存区域
   * 设置较小的Xmx128m
   * @throws Exception
   */
  @Test
  public void testStringPoolMemoryJdk18() throws Exception {
    int i = 1;
    while (true) {
      String.valueOf(i++).intern();
    }
  }
}
