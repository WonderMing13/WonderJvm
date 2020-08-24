package org.wonderming;

/**
 * @author wangdeming
 **/
public interface SpyHandler {

    Spy.SpyOfResult handleSpyMethodCallBefore(Object[] argumentArray, int targetClassLoaderId, int listenerId, String targetClassName, String targetMethodName, String targetDes, Object target);
}
