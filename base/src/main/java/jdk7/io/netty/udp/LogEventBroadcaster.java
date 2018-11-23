package jdk7.io.netty.udp;

import com.google.common.base.Throwables;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Listing 13.3 LogEventBroadcaster
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Slf4j
public class LogEventBroadcaster {
  private final EventLoopGroup group;
  private final Bootstrap bootstrap;
  private final File file;
  private Channel ch;
  protected String localHost;
  protected InetSocketAddress remoteAddress;

  public LogEventBroadcaster(String localHost, InetSocketAddress remoteAddress, File file) {
    group = new NioEventLoopGroup();
    bootstrap = new Bootstrap();
    bootstrap
        .group(group)
        .channel(NioDatagramChannel.class)
        .option(ChannelOption.SO_BROADCAST, true)
        .handler(new LogEventEncoder(remoteAddress));
    this.file = file;
    this.localHost = localHost;
    this.remoteAddress = remoteAddress;
  }

  public void run() throws Exception {
    //~ bind(0),底层由操作系统来分配一个空闲的端口号给进程监听,该Channel未来要作为发送端也是先bind本地
    ch = bootstrap.bind(localHost, 0).sync().channel();
    ch.closeFuture();
    log.info("{} started,local at {}", this.getClass().getSimpleName(), ch.localAddress());
    long pointer = 0;
    for (; ; ) {
      long len = file.length();
      if (len < pointer) {
        // file was reset
        pointer = len;
      } else if (len > pointer) {
        // Content was added
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.seek(pointer);
        String line;
        while ((line = raf.readLine()) != null) {
          //~ UDP不用去connect对方的地址，本地bind好了就可以给对方地址发送数据了
          ch.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
        }
        pointer = raf.getFilePointer();
        raf.close();
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.interrupted();
        break;
      }
    }
  }

  public void stop() throws InterruptedException, ExecutionException {
    ChannelFuture channelFuture = ch.close().sync();
    if (!channelFuture.isDone()) {
      Throwables.propagate(channelFuture.cause());
    }
    Future future = group.shutdownGracefully().sync();
    future.get();
  }

  public static void main(String[] args) throws Exception {
    String path = "e:\\gc.log";
    int remotePort = 9999;
    LogEventBroadcaster broadcaster =
        new LogEventBroadcaster(
            "127.0.0.1",
            //~ 局域网内广播发送
            new InetSocketAddress("255.255.255.255", remotePort),
            new File(path));
    try {
      broadcaster.run();
    } finally {
      broadcaster.stop();
    }
  }
}
