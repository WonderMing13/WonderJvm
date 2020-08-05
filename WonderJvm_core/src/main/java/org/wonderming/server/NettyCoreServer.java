package org.wonderming.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.wonderming.manager.CoreModuleManager;
import org.wonderming.manager.impl.DefaultCoreModuleManager;
import org.wonderming.model.CoreServerConfigure;
import org.wonderming.model.JvmModel;
import org.wonderming.server.handler.HttpServerHandler;
import org.wonderming.utils.JavaInstanceUtil;
import org.wonderming.utils.JavaMethodUtil;

import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangdeming
 **/
public class NettyCoreServer implements CoreServer{

    /**
     * Boss线程线程池
     */
    public static final ThreadFactory NAME_BOSS_THREAD_FACTORY = runnable -> {
        AtomicLong count = new AtomicLong(0);
        final Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setDaemon(false);
        thread.setName(String.format("WonderJvm-Boss",count.getAndIncrement()));
        return thread;
    };

    /**
     * Worker线程线程池
     */
    public static final ThreadFactory NAME_WORKER_THREAD_FACTORY = runnable -> {
        AtomicLong count = new AtomicLong(0);
        final Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setDaemon(true);
        thread.setName(String.format("WonderJvm-Worker",count.getAndIncrement()));
        return thread;
    };

    /**
     * Netty BOSS线程
     */
    private final static EventLoopGroup BOSS = new NioEventLoopGroup(1,NAME_BOSS_THREAD_FACTORY);

    /**
     * Netty Worker线程组 CPU核心 * 2
     */
    private final static EventLoopGroup WORK = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2,NAME_WORKER_THREAD_FACTORY);

    /**
     * 服务端ServerBootstrap
     */
    private final static ServerBootstrap SERVERBOOTSTRAP = new ServerBootstrap();

    /**
     * 返回Channel的结果
     */
    private  ChannelFuture channelFuture = null;

    /**
     * 绑定标识
     */
    private static volatile boolean BIND_FLAG = false;

    /**
     * CoreServer实例
     */
    private static volatile CoreServer coreServer;

    /**
     * JvmModel实例
     */
    private JvmModel jvmModel;

    public synchronized static CoreServer getInstance() {
        if (null == coreServer){
            synchronized (CoreServer.class){
                if (null == coreServer){
                    coreServer = new NettyCoreServer();
                }
            }
        }
        return coreServer;
    }

    static {
        SERVERBOOTSTRAP.group(BOSS,WORK);
        SERVERBOOTSTRAP.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        SERVERBOOTSTRAP.childOption(ChannelOption.TCP_NODELAY, true);
        SERVERBOOTSTRAP.childOption(ChannelOption.SO_KEEPALIVE, true);
        SERVERBOOTSTRAP.channel(NioServerSocketChannel.class);
    }

    private void init(Map<String, String> argsMap,Instrumentation inst) {
        //命名空间
        final String namespace = argsMap.get(CoreServerConfigure.NAME_SPACE);
        //system-module的主目录路径
        final String systemModulePath = argsMap.get(CoreServerConfigure.SYSTEM_MODULE);
        //user-module的主目录路径
        final String userModulePath = argsMap.get(CoreServerConfigure.USER_MODULE);
        //实例化JvmModel
        jvmModel = new JvmModel(systemModulePath,userModulePath,inst);
        //重置module列表
        jvmModel.getCoreModuleManager().reset();
        SERVERBOOTSTRAP.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel){
                channel.pipeline()
                        //将请求与应答消息编码或者解码为HTTP消息
                        .addLast(new HttpServerCodec())
                        //向客户端发送HTML5文件。主要用于支持浏览器和服务端进行WebSocket通信
                        .addLast(new ChunkedWriteHandler())
                        //将http消息的多个部分组合成一条完整的HTTP消息
                        .addLast(new HttpObjectAggregator(65535))
                        //三次握手协议的Http请求 处理token保存channel
                        .addLast("HttpServerHandler", new HttpServerHandler(namespace,jvmModel.getCoreModuleManager()))
                        //netty使用websocket协议
                        .addLast("WebSocketServerProtocolHandler", new WebSocketServerProtocolHandler(namespace));
            }
        });
    }

    @Override
    public boolean isBind() {
        return BIND_FLAG;
    }

    @Override
    public synchronized void bind(Map<String, String> argsMap,final Instrumentation inst) {
        final String ip = argsMap.get(CoreServerConfigure.SERVER_IP);
        final String port = argsMap.get(CoreServerConfigure.SERVER_PORT);
        final InetSocketAddress socketAddress = new InetSocketAddress(ip, Integer.parseInt(port));
        init(argsMap,inst);
        channelFuture = SERVERBOOTSTRAP.bind(socketAddress).addListener(future -> BIND_FLAG = future.isSuccess());
    }

    @Override
    public void destory() {
        try {
            if (null != channelFuture){
                channelFuture.channel().close().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            BOSS.shutdownGracefully();
            WORK.shutdownGracefully();
        }
    }


}
