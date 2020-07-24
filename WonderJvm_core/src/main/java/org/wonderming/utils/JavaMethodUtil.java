package org.wonderming.utils;

import java.lang.reflect.Method;

/**
 * @author wangdeming
 **/
public class JavaMethodUtil {

    public static Method getDeclaredJavaMethod(Class<?> clazz,String methodName,Class<?>... parameterClassArray){
        try {
            return clazz.getDeclaredMethod(methodName,parameterClassArray);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
