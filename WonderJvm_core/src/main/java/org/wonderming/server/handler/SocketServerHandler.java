package org.wonderming.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.wonderming.Module;
import org.wonderming.annotation.Command;
import org.wonderming.manager.CoreModuleManager;
import org.wonderming.model.CoreModuleModel;
import org.wonderming.model.CoreServerConfigure;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author wangdeming
 **/
public class SocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final CoreModuleManager coreModuleManager;

    public SocketServerHandler(CoreModuleManager coreModuleManager){
        this.coreModuleManager = coreModuleManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        final String path = channelHandlerContext.channel().attr(CoreServerConfigure.PATH).get();
        final String moduleId = parseModuleId(path);
        final CoreModuleModel coreModuleModel = coreModuleManager.get(moduleId);
        //获取该Module
        final Module module = coreModuleModel.getModule();
        final Method method = checkMethodAnnotation(module, path);
        if (method != null){
            final ClassLoader wonderJvmClassLoader = Thread.currentThread().getContextClassLoader();
            final boolean methodAccessible = method.isAccessible();
            try {
                method.setAccessible(true);
                //绕过类加载器的双亲委派模型
                Thread.currentThread().setContextClassLoader(coreModuleModel.getModuleJarClassLoader());
                method.invoke(module, (Object[]) method.getParameterTypes());
            }finally {
                Thread.currentThread().setContextClassLoader(wonderJvmClassLoader);
                method.setAccessible(methodAccessible);
            }
        }
    }

    /**
     * 提取模块ID
     * 模块ID应该在PATH的第一个位置
     *
     * @param path websocket访问路径path
     * @return 路径解析成功则返回模块的ID，如果解析失败则返回null
     */
    private String parseModuleId(final String path) {
        final String[] pathSegmentArray = StringUtils.split(path, "/");
        return ArrayUtils.getLength(pathSegmentArray) >= 1
                ? pathSegmentArray[0]
                : null;
    }

    /**
     * 校验methodValue的前面的"/"
     * @param methodValue 方法名称
     * @return 正确的方法名称
     */
    private String checkSlash(String methodValue){
        return methodValue.startsWith("/") ? methodValue : "/" + methodValue;
    }

    /**
     * 查找Module方法的@Command注解
     * @param module Spi Module
     * @param path Websocket请求路径
     * @return Method
     */
    private Method checkMethodAnnotation(Module module,String path){
        //查找Command注解
        for (Method method : MethodUtils.getMethodsListWithAnnotation(module.getClass(), Command.class)){
            final Command commandAnnotation = method.getAnnotation(Command.class);
            if (commandAnnotation == null){
                continue;
            }
            final String methodValue = checkSlash(commandAnnotation.methodValue());
            if ((checkSlash(Objects.requireNonNull(parseModuleId(path))) + methodValue).equals(path)){
                return method;
            }
        }
        return null;
    }
}
