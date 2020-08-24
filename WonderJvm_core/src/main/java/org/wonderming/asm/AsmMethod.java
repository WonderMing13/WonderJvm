package org.wonderming.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;
import org.wonderming.Spy;
import org.wonderming.utils.JavaMethodUtil;

import java.util.Objects;

/**
 * @author wangdeming
 **/
public interface AsmMethod {

     Type ASM_TYPE_SPY = Type.getType(Spy.class);

     Type ASM_TYPE_SPY_RESULT = Type.getType(Spy.SpyOfResult.class);

     Type ASM_TYPE_INT = Type.getType(int.class);

     Type ASM_TYPE_OBJECT = Type.getType(Object.class);

    Type ASM_TYPE_THROWABLE = Type.getType(Throwable.class);
    /**
     * 将要传给Spy的参数数组(类似堆)
     */
    Type OBJECT_TYPE = Type.getObjectType("java/lang/Object");

     static Method getAsmMethod(Class<?> clazz,
                                String methodName,
                                Class<?>... parameterClassArray){
         return Method.getMethod(JavaMethodUtil.getDeclaredJavaMethod(clazz, methodName, parameterClassArray));
      }

    /**
     * asm method of {@link Spy#spyMethodCallBefore(Object[], int, int, String, String, String, String, Object)}
     */
    Method ASM_SPY_METHOD_CALL_BEFORE = getAsmMethod(
             Spy.class,
             "spyMethodCallBefore",
             Object[].class,
             int.class,
             int.class,
             String.class,
             String.class,
             String.class,
             String.class,
             Object.class);
}
