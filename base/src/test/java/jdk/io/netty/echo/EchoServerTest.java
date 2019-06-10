package jdk.io.netty.echo;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author cctv 2018/3/5
 */
public class EchoServerTest {
  @Test
  public void testServer() throws Exception {
      String host = "127.0.0.1";
      int port = 12345;
      EchoServer echoServer = new EchoServer(port);
      EchoClient echoClient = new EchoClient(host,port);
      new Thread(new Runnable() {
          @Override
          public void run() {
              try {
                  echoServer.start();
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
      }).start();
      TimeUnit.MILLISECONDS.sleep(100);
      echoClient.start();
  }
}