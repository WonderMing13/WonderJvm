package org.wonderming.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.Arrays;

/**
 * @author wangdeming
 **/
public class EventMethod extends AdviceAdapter implements Opcodes {

    private final Type[] argumentTypeArray;


    protected EventMethod(MethodVisitor mv, int access, String name, String desc) {
        super(ASM7, mv, access, name, desc);
        this.argumentTypeArray = Type.getArgumentTypes(desc);
    }

}
