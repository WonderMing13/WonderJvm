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

    /**
     * xscx
     * @param clazz
     * @param methodName
     * @param parameterClassArray
     * @return
     */
     static Method getAsmMethod(Class<?> clazz,String methodName,Class<?>... parameterClassArray){
         return Method.getMethod(Objects.requireNonNull(JavaMethodUtil.getDeclaredJavaMethod(clazz, methodName, parameterClassArray)));
      }

     Method ASM_SPY_METHOD_CALL_BEFORE = getAsmMethod(Spy.class,"spyMethodCallBefore",Object[].class);
}
