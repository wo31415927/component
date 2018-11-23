package jdk7.io.netty;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.ScheduledFuture;

/** @author cctv 2018/3/5 */
public class MyNettyTest {
  ListeningExecutorService executorService =
      MoreExecutors.listeningDecorator(
          new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>()));

  @Test
  public void testGuavaFuture() throws Exception {
    ListenableFuture future =
        executorService.submit(
            new Runnable() {
              @Override
              public void run() {
                System.out.println("Runnable executed!d");
              }
            });
    future.addListener(
        new Runnable() {
          @Override
          public void run() {
            System.out.println("future Listener executed!");
          }
        },
        MoreExecutors.directExecutor());
    future.get();
  }

  @Test
  public void testNettyFuture() throws Exception {
    Channel channel = null;
    ChannelFuture future = channel.connect(new InetSocketAddress("192.168.0.1", 25));
    future.addListener(
        new ChannelFutureListener() {
          @Override
          public void operationComplete(ChannelFuture future) {
            if (future.isSuccess()) {
              ByteBuf buffer = Unpooled.copiedBuffer("Hello", Charset.defaultCharset());
              ChannelFuture wf = future.channel().writeAndFlush(buffer);
            } else {
              Throwable cause = future.cause();
              cause.printStackTrace();
            }
          }
        });
  }

  @Test
  public void testEventLoop() throws Exception {
    Channel channel = null;
    ScheduledFuture<?> future =
        channel
            .eventLoop()
            .scheduleAtFixedRate(
                new Runnable() {
                  @Override
                  public void run() {
                    System.out.println("Run every 60 seconds");
                  }
                },
                60,
                60,
                TimeUnit.SECONDS);
  }
}
