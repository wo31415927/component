package jdk.io;

import org.junit.Test;

import java.util.Scanner;

import jdk.io.nio.NioClient;
import jdk.io.nio.NioServer;

/** @author cctv 2018/2/27 */
public class NioTest {
  /**
   * JUnit Test configuration will only result in a console that doesn't receive inputs.
   * System.in必须使用main函数作为入口
   */
  @Test
  public void test1() throws Exception {
    main(null);
  }

  public static void main(String[] args) throws Exception {
    //运行服务器
    NioServer.start();
    //避免客户端先于服务器启动前执行代码
    Thread.sleep(100);
    //运行客户端
    NioClient.start();
    while (NioClient.sendMsg(new Scanner(System.in).nextLine())) ;
  }
}
