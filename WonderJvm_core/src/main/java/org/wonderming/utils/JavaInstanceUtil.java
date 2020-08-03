package org.wonderming.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author wangdeming
 **/
public class JavaInstanceUtil {

    public static final JavaInstanceUtil INSTANCE = new JavaInstanceUtil();

    @SuppressWarnings("unchecked")
    public  <T> T protectProxy(Class<T> instanceInterface,final T target){
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{instanceInterface}, (proxy, method, args) -> method.invoke(target,args));
    }
}
