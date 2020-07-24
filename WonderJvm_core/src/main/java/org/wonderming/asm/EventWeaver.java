package org.wonderming.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import static org.wonderming.asm.AsmMethod.ASM_SPY_METHOD_CALL_BEFORE;
import static org.wonderming.asm.AsmMethod.ASM_TYPE_SPY;

/**
 * @author wangdeming
 **/
public class EventWeaver extends ClassVisitor implements Opcodes {

    public EventWeaver(ClassVisitor cv) {
        super(ASM7, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new EventMethod(new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions),access,name,desc){
            private final Label beginLabel = new Label();

            @Override
            protected void onMethodEnter() {
                mark(beginLabel);
                loadArgArray();
                dup();
                invokeStatic(ASM_TYPE_SPY,ASM_SPY_METHOD_CALL_BEFORE);

            }

            @Override
            public void visitLineNumber(int line, Label start) {
                super.visitLineNumber(line, start);
            }
        };
    }

}
