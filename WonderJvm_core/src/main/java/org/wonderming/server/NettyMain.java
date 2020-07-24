package org.wonderming.server;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangdeming
 **/
public class NettyMain {

    public static void main(String[] args) {
        Map<String, String> argsMap = new HashMap<>();
        argsMap.put("namespace","namespace");
        argsMap.put("server.ip","127.0.0.1");
        argsMap.put("server.port","8010");
        final NettyCoreServer nettyCoreServer = new NettyCoreServer();
        nettyCoreServer.bind(argsMap);
    }
}
