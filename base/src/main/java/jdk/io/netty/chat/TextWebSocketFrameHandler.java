package jdk.io.netty.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Listing 12.2 Handling text frames
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Slf4j
public class TextWebSocketFrameHandler
    extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx,
        Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler
             .ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            //~ 用户自定义的ChannelHandler需要用户自己去除
            ctx.pipeline().remove(HttpRequestHandler.class);
            log.info("Client " + ctx.channel() + " joined");
            /*group.writeAndFlush(new TextWebSocketFrame(
                    "Client " + ctx.channel() + " joined"));*/
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
        TextWebSocketFrame msg) throws Exception {
        group.writeAndFlush(msg.retain());
    }
}
