package jdk7.io.netty.chat;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

/**
 * Listing 12.1 HTTPRequestHandler
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  private final String wsUri;
  private static final File INDEX;

  static {
    URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
    try {
      String path = location.toURI() + "index.html";
      //测试大文件传输
      //String path = location.toURI() + "1.zip";
      path = !path.contains("file:") ? path : path.substring(5);
      INDEX = new File(path);
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Unable to locate index.html", e);
    }
  }

  public HttpRequestHandler(String wsUri) {
    this.wsUri = wsUri;
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    if (wsUri.equalsIgnoreCase(request.getUri())) {
      ctx.fireChannelRead(request.retain());
    } else {
      if (HttpHeaders.is100ContinueExpected(request)) {
        send100Continue(ctx);
      }
      RandomAccessFile file = new RandomAccessFile(INDEX, "r");
      HttpResponse response =
          new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
      response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
      boolean keepAlive = HttpHeaders.isKeepAlive(request);
      if (keepAlive) {
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
        response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
      }
      ctx.write(response);
      if (ctx.pipeline().get(SslHandler.class) == null) {
        //零拷贝，内核到内核，不走用户空间
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
      } else {
        //需要SSL处理，所以要走用户空间
        ctx.write(new ChunkedNioFile(file.getChannel()));
      }
      //写回文件，必须写一个这个Content，表示文件结束
      ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
      if (!keepAlive) {
        //完成后关闭channel
        future.addListener(ChannelFutureListener.CLOSE);
      }
    }
  }

  private static void send100Continue(ChannelHandlerContext ctx) {
    FullHttpResponse response =
        new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
    ctx.writeAndFlush(response);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
