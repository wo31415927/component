package jdk.io;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.time.Clock;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import mockit.Expectations;
import mockit.integration.junit4.JMockit;

/** @author cctv 2018/2/10 */
@RunWith(JMockit.class)
public class MyRWTest {
  InputStream inputStream = new FileInputStream("E:\\data\\BigData\\dataset_613643\\613643\\posts");

  public MyRWTest() throws FileNotFoundException {}
  //      this.getClass().getResourceAsStream("/scanner.txt");
  /**
   * 1. threadsafe,为什么需要？read过程中很多position需要lock保护
   * 2. 能否动态修改delimiter？不行，readline()中\n是写死的
   *
   * @throws Exception
   */
  @Test
  public void testBufferReader() throws Exception {
    int threadSize = 2;
    ExecutorService service = Executors.newFixedThreadPool(threadSize);
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      for (int i = 0; i < threadSize; i++) {
        service.submit(
            new Runnable() {
              @Override
              public void run() {
                String row;
                try {
                  while (null != (row = reader.readLine())) {
                    System.out.println(row);
                  }
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            });
      }
      service.awaitTermination(60, TimeUnit.DAYS);
    }
  }

  /**
   * 1. 本测试案例为文件中不包含delimiter，charBuf会不断扩容的场景 2. 怎么利用缓冲区CharBuffer干活的
   * 3.Xmx4g,OOM的原因同testReadFile，charBuf翻倍扩容，最大需要3g内存
   *
   * @throws Exception
   */
  @Test
  public void testScanner() throws Exception {
    Readable readable = new InputStreamReader(inputStream);
    int bufferSize = 1024 * 1024 * 1024;
    String delimiter = "\n";
    try (Scanner scanner = new Scanner(readable)) {
      scanner.useDelimiter(delimiter);
      IOUtils.sizeScanner(scanner, bufferSize);
      String row;
      //略过空行
      Clock clock = Clock.systemDefaultZone();
      long start = clock.millis();
      while (scanner.hasNext() && !Strings.isNullOrEmpty(row = scanner.next())) {
        //        System.out.println(row);
      }
      System.out.println("耗时：" + (clock.millis() - start) / 1000.0);
    }
  }

  /**
   * 模拟IOException，用户手动检测异常
   *
   * @throws Exception
   */
  @Test(expected = IOException.class)
  public void testScannerException() throws Exception {
    Readable readable = new InputStreamReader(inputStream);
    new Expectations(readable) {
      {
        readable.read(withInstanceOf(CharBuffer.class));
        result = new IOException();
      }
    };
    int bufferSize = 1024 * 1024;
    String delimiter = "\n\n\n";
    try (Scanner scanner = new Scanner(readable)) {
      scanner.useDelimiter(delimiter);
      IOUtils.sizeScanner(scanner, bufferSize);
      String row;
      Clock clock = Clock.systemDefaultZone();
      long start = clock.millis();
      while (scanner.hasNext()
          && !Strings.isNullOrEmpty(row = scanner.next())
          && IOUtils.checkException(scanner)) {}

      IOUtils.checkException(scanner);

      System.out.println("耗时：" + (clock.millis() - start) / 1000.0);
    }
  }

  /**
   * 为什么读取660m的文件，-Xmx4g(-Xmx8g问题解决)时会OOM？
   *
   * <p>1）根据jdk8 jvm默认参数（jmap -heap
   * pid查看,比使用jstat查看要更清晰）：NewRatio=2，老年代是年轻代的两倍空间，所以，Old=2730m，Young=1365m
   * 2）当StringBuilder内部的char[]增长到512m时，此时再次append(64m),StringBuilder使用了Arrays.copyOf进行扩容，该函数会首先定义一个数组
   * char[] newArr = new
   * char[cap*2],然后使用System.arraycopy将旧内容复制到新数组中，然而，定义完数组后，此时内存中有新数组(cap*2)和老数组(cap),
   * 共需cap*3的char内存，即new
   * char[cap*2]时会需要512m*2=1024m,char[1024m]占用2g内存，Old中此时有旧数组1g内存，放不下新的2g内存，所以OOM了
   * 3）大数组处理，使用Arrays.copyOf一定要小心，大字符串使用StringBuilder一定要小心
   * 4）为什么660m的文件读到内存中刚好占用char[660m]?因为文件基本上全是英文字符，一个byte就是一个char，都按默认的UTF-8处理
   *
   * @throws Exception
   */
  @Test
  public void testReadFile() throws Exception {
    System.out.println(Charset.defaultCharset());
    int bufSize = 1024 * 1024 * 64;
    StringBuilder sb = new StringBuilder();
    char[] cArr = new char[bufSize];
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), bufSize)) {
      int cnt;
      while ((cnt = reader.read(cArr)) >= 0) {
        sb.append(cArr, 0, cnt);
        double space = sb.capacity() / 1024.0 / 1024 * 2;
        System.out.println("内存空间(M): " + space);
        //toString()内部会初始化一个char[],导致内存翻倍，所以不能toString()
        //double space = sb.toString().getBytes().length / 1024.0 / 1024;
      }
    }
    System.out.println(sb.toString().getBytes().length);
  }

  public void add(List<String> l) {
    l = Lists.newLinkedList();
  }

  @Test
  public void testMethod() throws Exception {
    List<String> l1 = Lists.newArrayList("abc");
    add(l1);
    System.out.println(l1);
  }
}
