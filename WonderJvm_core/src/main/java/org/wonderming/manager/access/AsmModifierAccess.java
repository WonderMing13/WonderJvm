package org.wonderming.manager.access;

import org.apache.commons.lang3.ArrayUtils;
import org.wonderming.manager.CoreAccess;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author wangdeming
 **/
public class AsmModifierAccess implements CoreAccess {

    private final int access;

    public AsmModifierAccess(int access) {
        this.access = access;
    }

    private int getAccess(){
        return access;
    }

    @Override
    public boolean isPublic() {
        return isIn(getAccess(),ACC_PUBLIC);
    }

    @Override
    public boolean isPrivate() {
        return isIn(getAccess(),ACC_PRIVATE);
    }

    @Override
    public boolean isProtected() {
        return isIn(getAccess(),ACC_PROTECTED);
    }

    @Override
    public boolean isStatic() {
        return isIn(getAccess(),ACC_STATIC);
    }

    @Override
    public boolean isFinal() {
        return isIn(getAccess(),ACC_FINAL);
    }

    @Override
    public boolean isInterface() {
        return isIn(getAccess(),ACC_INTERFACE);
    }

    @Override
    public boolean isNative() {
        return isIn(getAccess(),ACC_NATIVE);
    }

    @Override
    public boolean isAbstract() {
        return isIn(getAccess(),ACC_ABSTRACT);
    }

    @Override
    public boolean isEnum() {
        return isIn(getAccess(),ACC_ENUM);
    }

    @Override
    public boolean isAnnotation() {
        return isIn(getAccess(),ACC_ANNOTATION);
    }

    public static boolean isIn(int target,int... maskArray){
        if (ArrayUtils.isEmpty(maskArray)){
            return false;
        }
        for (int mask : maskArray){
            //16进制进行&操作
            if ((target & mask) == mask){
                return true;
            }
        }
        return false;
    }
}
