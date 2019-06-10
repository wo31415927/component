package jdk.io.netty.echo;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

/** @author cctv 2018/3/5 */
public class EchoClient {
  private final String host;
  private final int port;

  public EchoClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void start() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      final AttributeKey<Integer> id = new AttributeKey<Integer>("ID");
      Bootstrap b = new Bootstrap();
      b.group(group)
          .channel(NioSocketChannel.class)
          .remoteAddress(new InetSocketAddress(host, port))
          .handler(
              new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                  //~ 对pipeline来说，inbound一般放在头部，因为遍历是从头部开始遍历的，但放在尾部也可
                  ch.pipeline().addFirst(new EchoClientHandler());
                  //获取预先设置的属性
                  System.out.println(ch.attr(id).get());
                }
              });
      //设置属性
      b.attr(id, 123456);
      ChannelFuture f = b.connect().sync();
      f.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully().sync();
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("Usage: " + EchoClient.class.getSimpleName() + " <host> <port>");
      return;
    }
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    new EchoClient(host, port).start();
  }
}
