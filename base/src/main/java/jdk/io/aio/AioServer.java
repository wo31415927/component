package jdk.io.aio;

/** @author cctv 2018/2/27 */
public class AioServer {
  private static int DEFAULT_PORT = 12345;
  private static AsyncServerHandler serverHandler;
  public static volatile long clientCount = 0;

  public static void start() {
    start(DEFAULT_PORT);
  }

  public static synchronized void start(int port) {
    if (serverHandler != null) {
      return;
    }
    serverHandler = new AsyncServerHandler(port);
    new Thread(serverHandler, "AioServer").start();
  }

  public static void main(String[] args) {
    AioServer.start();
  }
}
