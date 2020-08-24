package org.wonderming;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 藏匿于BootstrapClassLoader的间谍类
 * @author wangdeming
 **/
public class Spy {
    /**
     * 全局序列
     */
    private static final AtomicInteger SEQUENCER_EF = new AtomicInteger(1000);

    /**
     * 记录线程
     */
    private static final Map<Long,Thread> THREAD_MAP = new ConcurrentHashMap<Long, Thread>();

    public static final ConcurrentHashMap<String, SpyHandler> NAMESPACE_SPY_HANDLER_MAP
            = new ConcurrentHashMap<String, SpyHandler>();


    /**
     * 一个方法被调用之前
     */
    public static SpyOfResult spyMethodCallBefore(Object[] argumentArray,
                                                  int targetClassLoaderId,
                                                  int listenerId,
                                                  String namespace,
                                                  String targetClassName,
                                                  String targetMethodName,
                                                  String targetDes,
                                                  Object target){
        final SpyHandler spyHandler = NAMESPACE_SPY_HANDLER_MAP.get(namespace);
        return spyHandler.handleSpyMethodCallBefore(
                argumentArray,
                targetClassLoaderId,
                listenerId,
                targetClassName,
                targetMethodName,
                targetDes,
                target);
    }

    /**
     * 一个方法被调用正常返回之后
     */
    public static void spyMethodCallReturn(){

    }

    /**
     * 一个方法被调用抛出异常之后
     */
    public static void spyMethodCallThrows(){

    }

    /**
     * 执行方法体之前被调用
     */
    public static void spyMethodOnsBefore(){

    }

    /**
     * 执行方法体返回之前被调用
     */
    public static void spyMethodOnReturn(){

    }

    /**
     * 执行方法体抛出异常之前被调用
     */
    public static void spyMethodOnThrows(){

    }

    public static class SpyOfResult{
        //void
        public static final int SPY_RESULT_STATE_NOTHING = 0;
        //正常返回对象
        public static final int SPY_RESULT_STATE_RETURN = 1;
        //返回的是异常
        public static final int SPY_RESULT_STATE_THROWS = 2;

        /**
         * 状态码
         */
        public final int state;

        /**
         * 返回的应答对象
         */
        public final Object response;

        public SpyOfResult(int state,Object response) {
            this.state = state;
            this.response = response;
        }
    }

    public static class SpyThreadHolder{
        /**
         * 将线程存放在内存中
         * @param thread 当前线程
         */
        public static void enter(Thread thread){
            THREAD_MAP.put(thread.getId(),thread);
        }

        /**
         * 清空存储在内存中的线程
         */
        public static void init(){
            final Set<Long> keySet = THREAD_MAP.keySet();
            for (Long threadId:keySet) {
                THREAD_MAP.remove(threadId);
            }
        }

        /**
         * 判断该线程是否已经进入过内存
         * @param thread 当前线程
         * @return true:已经进入到内存中 false:还未进入到内存中
         */
        public static boolean isEnter(Thread thread){
            return THREAD_MAP.get(thread.getId()) != null;
        }

        /**
         * 清除已经完成使命的线程
         * @param thread 完成使命的线程
         */
        public static void delete(Thread thread){
            final Thread vmThread = THREAD_MAP.get(thread.getId());
            if (vmThread != null){
                THREAD_MAP.remove(vmThread.getId());
            }
        }
    }

    public static int nextSequence(){
        return SEQUENCER_EF.getAndIncrement();
    }
}
