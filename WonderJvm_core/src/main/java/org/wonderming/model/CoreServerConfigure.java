package org.wonderming.model;

import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangdeming
 **/
public class CoreServerConfigure {

    public final static String WEB_SOCKET = "/ws";

    public final static String NAME_SPACE = "namespace";

    public final static String SERVER_IP = "server.ip";

    public final static String SERVER_PORT = "server.port";

    public final static String SYSTEM_MODULE = "system.module";

    public final static String USER_MODULE = "user.module";

    public final static String HTTP_PARAM = "path";

    public final static AttributeKey<String> PATH = AttributeKey.valueOf("path");

//    public static void putChannelToMap(String namespace, String path, NioSocketChannel channel) {
//        final List<NioSocketChannel> nioSocketChannelList = CHANNEL_MAP.get(namespace);
//        if (nioSocketChannelList != null && !nioSocketChannelList.isEmpty()) {
//            final boolean anyMatch = nioSocketChannelList.parallelStream().anyMatch(nioSocketChannel -> nioSocketChannel.attr(PATH).get().equals(path));
//            //namespace下面没有此路径
//            if (!anyMatch) {
//                nioSocketChannelList.add(channel);
//                CHANNEL_MAP.put(namespace, nioSocketChannelList);
//            }
//        }else {
//            final List<NioSocketChannel> nioSocketChannels = new ArrayList<>();
//            nioSocketChannels.add(channel);
//            CHANNEL_MAP.put(namespace,nioSocketChannels);
//        }
//    }
//
//    public static NioSocketChannel getChannelByNameSpace(String namespace, String path) {
//        final List<NioSocketChannel> nioSocketChannelList = CHANNEL_MAP.get(namespace);
//        return nioSocketChannelList
//                .parallelStream()
//                .filter(nioSocketChannel -> nioSocketChannel.attr(PATH).get().equals(path))
//                .findFirst().get();
//    }


}
