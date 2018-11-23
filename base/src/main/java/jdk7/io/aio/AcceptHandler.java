package jdk7.io.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/** @author cctv 2018/3/5 */
public class AcceptHandler
    implements CompletionHandler<AsynchronousSocketChannel, AsyncServerHandler> {
  @Override
  public void completed(AsynchronousSocketChannel channel, AsyncServerHandler serverHandler) {
    //继续接受其他客户端的请求
    AioServer.clientCount++;
    System.out.println("连接的客户端数：" + AioServer.clientCount);
    serverHandler.channel.accept(serverHandler, this);
    //创建新的Buffer
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    //~ 内核是一个EventBus，注册一个关心的事件，随后事件就绪后，开启新线程执行回调方法，win下IOCP是新线程回调
    // 异步读  第三个参数为接收消息回调的业务Handler
    channel.read(buffer, buffer, new ReadHandler(channel));
  }

  @Override
  public void failed(Throwable exc, AsyncServerHandler serverHandler) {
    exc.printStackTrace();
    serverHandler.latch.countDown();
  }
}
