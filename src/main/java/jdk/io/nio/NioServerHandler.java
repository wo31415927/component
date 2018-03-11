package jdk.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import jdk.io.util.Calculator;
import lombok.extern.slf4j.Slf4j;

/** @author cctv 2018/2/27 */
@Slf4j
public class NioServerHandler implements Runnable {
  private Selector selector;
  private ServerSocketChannel serverChannel;
  private volatile boolean started;
  /**
   * 构造方法
   *
   * @param port 指定要监听的端口号
   */
  public NioServerHandler(int port) {
    try {
      //~从sun.nio.ch.DefaultSelectorProvider的源码中,我们可以看到,如果是linux机器,并且其内核版本大于2.6,
      //~创建的就是EPollSelectorProvider,否则的话,就创建PollSelectorProvider.默认是lt
      //创建选择器
      selector = Selector.open();
      //打开监听通道
      serverChannel = ServerSocketChannel.open();
      //如果为 true，则此通道将被置于阻塞模式；如果为 false，则此通道将被置于非阻塞模式
      serverChannel.configureBlocking(false); //开启非阻塞模式
      //绑定端口 backlog设为1024
      serverChannel.socket().bind(new InetSocketAddress(port), 1024);
      //监听客户端连接请求
      //可以注册多个Selector吗?按epoll的原理似乎是可以的
      serverChannel.register(selector, SelectionKey.OP_ACCEPT);
      //标记服务器已开启
      started = true;
      log.info("服务器已启动，端口号：" + port);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void stop() {
    started = false;
  }

  @Override
  public void run() {
    //循环遍历selector
    while (started) {
      try {
        //Interrupt时会设置Interrupted状态并立即返回，返回值随机
        //timeout可以不用设置
        selector.select();
        Set<SelectionKey> keys = selector.selectedKeys();
        Iterator<SelectionKey> it = keys.iterator();
        SelectionKey key = null;
        while (it.hasNext()) {
          key = it.next();
          try {
            //~ 假如不处理key，那么每次selector.selectedKeys()都将返回AcceptKey，所以是LT
            //是否remove()无差别
            handleInput(key);
            //          it.remove();
          } catch (Exception e) {
            if (key != null) {
              key.cancel();
              if (key.channel() != null) {
                key.channel().close();
              }
            }
          }
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
    //selector关闭后会自动释放里面管理的资源
    if (selector != null) {
      try {
        selector.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void handleInput(SelectionKey key) throws IOException {
    if (key.isValid()) {
      //处理新接入的请求消息
      if (key.isAcceptable()) {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        //通过ServerSocketChannel的accept创建SocketChannel实例
        //完成该操作意味着完成TCP三次握手，TCP物理链路正式建立
        SocketChannel sc = ssc.accept();
        //设置为非阻塞的
        sc.configureBlocking(false);
        //注册为读
        sc.register(selector, SelectionKey.OP_READ);
      }
      //读消息
      if (key.isReadable()) {
        SocketChannel sc = (SocketChannel) key.channel();
        //创建ByteBuffer，并开辟一个1k的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //读取请求码流，返回读取到的字节数
        int readBytes;
        //~ 用while比原先用if要强，input buffer中的数据循环取完直到无数据可取，没必要每次依赖select触发
        while ((readBytes = sc.read(buffer)) > 0) {
          //将缓冲区当前的limit设置为position=0，用于后续对缓冲区的读取操作
          buffer.flip();
          //根据缓冲区可读字节数创建字节数组
          byte[] bytes = new byte[buffer.remaining()];
          //将缓冲区可读字节数组复制到新建的数组中
          buffer.get(bytes);
          String expression = new String(bytes, "UTF-8");
          log.info("服务器收到消息：" + expression);
          //处理数据
          String result = null;
          try {
            result = Calculator.cal(expression).toString();
          } catch (Exception e) {
            result = "计算错误：" + e.getMessage();
          }
          //发送应答消息
          //! 会阻塞哈
          doWrite(sc, result);
        }
        if (readBytes < 0) {
          key.cancel();
          sc.close();
        }
      }
    }
  }
  //异步发送应答消息
  private void doWrite(SocketChannel channel, String response) throws IOException {
    //将消息编码为字节数组
    byte[] bytes = response.getBytes();
    //根据数组容量创建ByteBuffer
    ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
    //将字节数组复制到缓冲区
    writeBuffer.put(bytes);
    //flip操作
    writeBuffer.flip();
    //发送缓冲区的字节数组
    //non-blocking channel，假如output buffer未满，则很可能写入0字节，此时response就丢了
    channel.write(writeBuffer);
    //****此处不含处理“写半包”的代码
  }
}
