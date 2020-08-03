package org.wonderming;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.jar.JarFile;

/**
 * @author wangdeming
 **/
public class AgentLauncher {

    /**
     * 执行Java的某个类
     * -cp WonderJvm_agent/target/WonderJvm_agent-1.0-SNAPSHOT-jar-with-dependencies.jar
     * agent方式启动
     * -javaagent:WonderJvm_agent/target/WonderJvm_agent-1.0-SNAPSHOT-jar-with-dependencies.jar=namespace=namespace&server.ip=127.0.0.1&server.port=8010
     * @param args 传入的参数
     * @param inst Instrumentation JMTI的插桩
     */
    public static void premain(String args, Instrumentation inst){
        install(toArgsMap(args),inst);
    }

    /**
     * attach方式启动
     * 需要lib tools jar包来加载
     * @param args 传入的参数
     * @param inst Instrumentation JMTI的插桩
     */
    public static void agentmain(String args,Instrumentation inst){
        install(toArgsMap(args),inst);
    }


    public static void install(final Map<String,String> toArgsMap, final Instrumentation inst) {
        try {
            //将间谍类Spy加载到BootstrapClassLoader
            inst.appendToBootstrapClassLoaderSearch(new JarFile(new File(getWonderJvmLibJarPath("WonderJvm-spy.jar"))));
            //WonderClassLoader加载Core
            final WonderClassLoader wonderClassLoader = loadJarToWonderClassLoader(toArgsMap.get(NAME_SPACE));
            //加载NettyCoreServer类
            final Class<?> nettyCoreServer = wonderClassLoader.loadClass("org.wonderming.server.NettyCoreServer");
            //CoreServer实例
            final Object coreServerInstance = nettyCoreServer.getMethod("getInstance").invoke(null);
            //获取NettyServer绑定标识
            final Boolean isBind = (Boolean) nettyCoreServer.getMethod("isBind").invoke(coreServerInstance);
            if (!isBind){
                //没有绑定啊 那就启动Netty咯
                nettyCoreServer.getMethod("bind",Map.class,Instrumentation.class).invoke(coreServerInstance,toArgsMap,inst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析传来的参数封装成Map
     * @param args premain和agentmain传入的参数
     * @return 封装的Map
     */
    public static Map<String,String> toArgsMap(String args){
        //使用LinkedHashMap顺序存储
        Map<String,String> argsMap = new LinkedHashMap<String, String>();
        final String[] kvPairSegmentArray = args.split("&");
        //如果没有 返回空map
        if (kvPairSegmentArray.length == 0){
            return argsMap;
        }
        for (int i = kvPairSegmentArray.length - 1; i >= 0; i--) {
            final String[] kvSegmentArray = kvPairSegmentArray[i].split("=");
            argsMap.put(kvSegmentArray[0],kvSegmentArray[1]);
        }
        argsMap.put(SYSTEM_MODULE,getWonderJvmSystemModuleJarPath());
        argsMap.put(USER_MODULE,getWonderJvmUserModuleJarPath());
        return argsMap;
    }

    public final static String NAME_SPACE = "namespace";

    public final static String SYSTEM_MODULE = "system.module";

    public final static String USER_MODULE = "user.module";

    private static final ReentrantReadWriteLock RW = new ReentrantReadWriteLock();

    public static final Map<String/*namespace**/,WonderClassLoader> WONDER_CLASS_LOADER_MAP = new HashMap<>();

    /**
     * 一个namespace对应一个自定义ClassLoader并且同时启动了一个CoreServer
     * @param namespace 命名空间
     * @return WonderClassLoader
     * @throws MalformedURLException 异常
     */
    public static WonderClassLoader loadJarToWonderClassLoader(String namespace) throws MalformedURLException {
        RW.writeLock().lock();
        final WonderClassLoader wonderClassLoader;
        if (WONDER_CLASS_LOADER_MAP.get(namespace) != null){
            wonderClassLoader = WONDER_CLASS_LOADER_MAP.get(namespace);
        }else {
            wonderClassLoader = new WonderClassLoader(getWonderJvmLibJarPath("WonderJvm-core.jar"));
            WONDER_CLASS_LOADER_MAP.put(namespace,wonderClassLoader);
        }
        RW.writeLock().unlock();
        return wonderClassLoader;
    }

    /**
     * 获取~./lib/Jar的完整路径
     * @param jarName 要获取Jar包名称
     * @return Jar包的完整路径
     * <p>
     *     File.separatorChar:Windows下是'\\'
     *     File.separator:是File.separatorChar的字符串形式 可以在字符串中链接使用
     * </p>
     */
    public static String getWonderJvmLibJarPath(String jarName){
        return getWonderJvmHome() + File.separatorChar + "lib" + File.separator + jarName;
    }

    /**
     * 获取~./system-module/下的jar包的路径
     * @return Jar包的完整路径
     */
    public static String getWonderJvmSystemModuleJarPath(){
        return getWonderJvmHome() + File.separatorChar + "module" + File.separator + "system-module";
    }

    /**
     * 获取~./user-module/下的jar包的路径
     * @return Jar包的完整路径
     */
    public static String getWonderJvmUserModuleJarPath(){
        return getWonderJvmHome() + File.separatorChar + "module" + File.separator + "user-module";
    }

    /**
     * 获取WonderJvm_agent jar包所在主目录
     * @return String 打包成的路径
     */
    public static String getWonderJvmHome(){
         return new File(
                  AgentLauncher.class
                 .getProtectionDomain()
                 .getCodeSource()
                 .getLocation().getFile()).getParentFile().getParent();
    }


}
