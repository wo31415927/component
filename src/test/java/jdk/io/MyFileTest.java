package jdk.io;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

/** @author zeyu 2017/12/6 */
public class MyFileTest {
  @Test
  public void testFile() throws Exception {
    System.out.println(File.separator);
    System.out.println(File.pathSeparator);
  }

  @Test
  public void testLoadFile() throws Exception {
    /* String s = new String(CipherUtils.hex2ASCIIByte("0x05".substring(2).getBytes()));
    String des = s+"Hello"+s+"World"+s;
    System.out.println(s);
    System.out.println(Splitter.on(s).splitToList(des));
    System.out.println(des.startsWith(s) && des.endsWith(s));*/
    //竟然无法使用绝对路径
    try (InputStream inputStream =
        MyFile.class.getResourceAsStream("D:\\tmp\\mytmp\\apple1\\testcolumn\\testcolumn.txt")) {
      System.out.println(inputStream);
    }
    try (InputStream inputStream =
        new FileInputStream("D:\\tmp\\mytmp\\apple1\\testcolumn\\testcolumn.txt")) {
      System.out.println(inputStream);
    }
  }

  /**
   * 两种数组的格式是不一样
   *
   * @throws Exception
   */
  @Test
  public void testArray() throws Exception {
    Object[][] arr1 = {{1, 2}, {3, 4}};
    Object[] arr2 = new Object[2];
    arr2[0] = new Object[] {1, 2};
    arr2[1] = new Object[] {3, 4};
    System.out.println(Arrays.equals(arr1, arr2));
  }
}
