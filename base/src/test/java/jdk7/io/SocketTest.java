package jdk7.io;

import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

/** @author cctv 2018/2/25 */
public class SocketTest {
  String host = "127.0.0.1";

  @Test
  public void testParam() throws Exception {
    Socket socket = new Socket();
    socket.connect(new InetSocketAddress(host, 8000));
    InputStream in = socket.getInputStream();
    OutputStream out = socket.getOutputStream();
    String head = "hello ";
    String body = "world\r\n";
    out.write(head.getBytes());
    out.write(body.getBytes());
    in.read();
    socket.close();
  }

  @Test
  public void testSelector() throws Exception {
    // 打开一个通道
    SocketChannel channel = SocketChannel.open();
    // 发起连接
    channel.connect(new InetSocketAddress("https://www.javadoop.com", 80));
    channel.configureBlocking(false);
    Selector selector = Selector.open();
    channel.register(selector, SelectionKey.OP_READ);
    while (true) {
      // 判断是否有事件准备好
      int readyChannels = selector.select();
      if (readyChannels == 0) continue;

      // 遍历
      Set<SelectionKey> selectedKeys = selector.selectedKeys();
      Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        if (key.isAcceptable()) {
          // a connection was accepted by a ServerSocketChannel.
        } else if (key.isConnectable()) {
          // a connection was established with a remote server.
        } else if (key.isReadable()) {
          // a channel is ready for reading
        } else if (key.isWritable()) {
          // a channel is ready for writing
        }
        keyIterator.remove();
      }
    }
  }

    @Test
    public void testNonBlocking() throws Exception {
        Path path = Paths.get("");
        // 打开一个通道
        try(SocketChannel channel = SocketChannel.open()) {
            // 发起连接
            channel.connect(new InetSocketAddress("https://www.javadoop.com", 80));
            channel.configureBlocking(false);
        }
        try(FileChannel fileChannel = FileChannel.open(path)){
            //FileChannel没有非阻塞
        }
        try(InputStream inputStream = Files.newInputStream(path)){
            inputStream.read();
        }
    }
}
