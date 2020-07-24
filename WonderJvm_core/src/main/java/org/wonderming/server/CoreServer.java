package org.wonderming.server;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author wangdeming
 **/
public interface CoreServer {

    /**
     * 是否绑定
     * @return true:绑定成功 false:绑定失败
     */
    public boolean isBind();

    /**
     * 绑定
     */
    public void bind(Map<String, String> argsMap);

    /**
     * 注销CoreServer
     */
    public void destory();
}
