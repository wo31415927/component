package jdk.base;

import org.junit.Test;

/** @author cctv 2018/1/5 */
public class MyOperatorTest {
  /**
   *
   >> 是带符号右移，若左操作数是正数，则高位补“0”，若左操作数是负数，则高位补“1”.

   << 将左操作数向左边移动，并且在低位补0.

   >>> 是无符号右移，无论左操作数是正数还是负数，在高位都补“0”
   * @throws Exception
   */
  @Test
  public void testBit() throws Exception {
    int number = 10;
    //原始数二进制
    printInfo(number);
    number = number << 1;
    //左移一位
    printInfo(number);
    number = number >> 1;
    //右移一位
    printInfo(number);
    number = number >>> 1;
    printInfo(number);
    number = -10;
    printInfo(number);
    printInfo(number >> 1);
    System.out.println(number >> 1);
    printInfo(number >>> 1);
  }

  /**
   *
   * @throws Exception
   */
  @Test
  public void testMoveBit() throws Exception {
    //0xff是int
    System.out.println(0xff >>> 7);
    //(byte) 0xff是补码,移位操作前先转换为int（32位），然后再做移位
    System.out.println((byte) 0xff >>> 7);
    //对int做byte转换，相当于截取低8位做补码，打印时，由补码转换为原码的值再打印
    System.out.println((byte) (((byte) 0xff) >>> 7));
  }

  /**
   * 输出一个int的二进制数
   *
   * @param num
   */
  private static void printInfo(int num) {
    System.out.println(Integer.toBinaryString(num));
  }
}
