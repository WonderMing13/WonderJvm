package org.wonderming.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.wonderming.model.CoreServerConfigure;

/**
 * @author wangdeming
 **/
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String namespace;

    public HttpServerHandler(String namespace){
         this.namespace = namespace;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        final String uri = fullHttpRequest.uri();
        if (uri.contains(CoreServerConfigure.WEB_SOCKET)){
            fullHttpRequest.setUri(namespace);
            channelHandlerContext.pipeline().addAfter("WebSocketServerProtocolHandler","WebSocketServerHandler",new SocketServerHandler());
            channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
        }
    }
}
