package org.wonderming.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import org.wonderming.manager.CoreModuleManager;
import org.wonderming.model.CoreServerConfigure;
import org.wonderming.utils.FullHttpRequestUtil;

import java.util.Map;

/**
 * @author wangdeming
 **/
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String namespace;

    private final CoreModuleManager coreModuleManager;

    public HttpServerHandler(String namespace,CoreModuleManager coreModuleManager){
         this.namespace = namespace;
         this.coreModuleManager = coreModuleManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest){
        final String uri = fullHttpRequest.uri();
        if (uri.contains(CoreServerConfigure.WEB_SOCKET)){
            init(channelHandlerContext,fullHttpRequest);
            fullHttpRequest.setUri(namespace);
            channelHandlerContext.pipeline().addAfter("WebSocketServerProtocolHandler","WebSocketServerHandler",new SocketServerHandler(coreModuleManager));
            channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
        }
    }

    private void init(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest){
        final Map<String, String> param = FullHttpRequestUtil.parseParam(fullHttpRequest);
        final String path = param.get(CoreServerConfigure.HTTP_PARAM);
        channelHandlerContext.channel().attr(CoreServerConfigure.PATH).set(path);
    }
}
