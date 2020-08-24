package org.wonderming.utils;

import java.lang.reflect.Method;

/**
 * @author wangdeming
 **/
public class JavaMethodUtil {

    public static Method getDeclaredJavaMethod(Class<?> clazz,
                                               String methodName,
                                               Class<?>... parameterClassArray){
        try {
            return clazz.getDeclaredMethod(methodName,parameterClassArray);
        } catch (NoSuchMethodException e) {
            throw new UnCaughtException(e);
        }
    }

    public static class UnCaughtException extends RuntimeException {

        public UnCaughtException(Throwable cause) {
            super(cause);
        }
    }
}
