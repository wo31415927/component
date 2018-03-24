package jdk.io.netty.chat;

import org.junit.Test;

import java.lang.reflect.Method;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

/** @author cctv 2018/3/9 */
public class HttpRequestHandlerTest {
  @Test
  public void channelRead0() throws Exception {
    ChannelInboundHandler handler = new HttpRequestHandler("");
    Method m =
        HttpRequestHandler.class.getDeclaredMethod(
            "channelRead0", ChannelHandlerContext.class, FullHttpRequest.class);
    m.setAccessible(true);
    m.invoke(handler, null, "");
  }
}
