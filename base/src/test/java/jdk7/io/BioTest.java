package jdk7.io;

import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import jdk7.io.bio.BioClient;
import jdk7.io.bio.BioServer;

/** @author cctv 2018/2/27 */
public class BioTest {
  @Test
  public void test1() throws Exception {
    //运行服务器
    new Thread(
            () -> {
              try {
                BioServer.start();
              } catch (IOException e) {
                e.printStackTrace();
              }
            })
        .start();
    //避免客户端先于服务器启动前执行代码
    Thread.sleep(100);
    //运行客户端
    char operators[] = {'+', '-', '*', '/'};
    Random random = new Random(System.currentTimeMillis());
    Thread client =
        new Thread(
                () -> {
                  while (true) {
                    //随机产生算术表达式
                    String expression =
                        random.nextInt(10)
                            + ""
                            + operators[random.nextInt(4)]
                            + (random.nextInt(10) + 1);
                    BioClient.send(expression);
                    try {
                      Thread.currentThread().sleep(random.nextInt(1000));
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                  }
                });
    client.start();
    client.join();
  }
}
