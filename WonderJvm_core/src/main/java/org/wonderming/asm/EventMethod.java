package org.wonderming.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.Arrays;

/**
 * @author wangdeming
 **/
public class EventMethod extends AdviceAdapter implements Opcodes,AsmMethod {

    private final Type[] argumentTypeArray;

    protected EventMethod(MethodVisitor mv, int access, String name, String desc) {
        super(ASM7, mv, access, name, desc);
        this.argumentTypeArray = Type.getArgumentTypes(desc);
    }

    protected void loadThisOrPushNullIfIsStatic(){
        if ((methodAccess & ACC_STATIC) != 0){
            push((Type)null);
        }else {
            loadThis();
        }
    }

    final protected void checkCastReturn(Type returnType) {
        final int sort = returnType.getSort();
        switch (sort) {
            case Type.VOID: {
                pop();
                mv.visitInsn(Opcodes.RETURN);
                break;
            }
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT: {
                unbox(returnType);
                returnValue();
                break;
            }
            case Type.FLOAT: {
                unbox(returnType);
                mv.visitInsn(Opcodes.FRETURN);
                break;
            }
            case Type.LONG: {
                unbox(returnType);
                mv.visitInsn(Opcodes.LRETURN);
                break;
            }
            case Type.DOUBLE: {
                unbox(returnType);
                mv.visitInsn(Opcodes.DRETURN);
                break;
            }
            case Type.ARRAY:
            case Type.OBJECT:
            case Type.METHOD:
            default: {
                // checkCast(returnType);
                unbox(returnType);
                mv.visitInsn(ARETURN);
                break;
            }

        }
    }

    final protected void visitFieldInsn(int opcode, Type owner, String name, Type type) {
        super.visitFieldInsn(opcode, owner.getInternalName(), name, type.getDescriptor());
    }

//    @Override
//    public void loadArgArray() {
//        //将参数组的长度放进操作栈中
//        push(argumentTypeArray.length);
//        //消耗一个长度count
//        //and a reference arrayref to this new array object is pushed onto the operand stack.
//        //一个数组引用会加载到操作数栈中
//        newArray(OBJECT_TYPE);
//        for (int i = 0; i < argumentTypeArray.length; i++) {
//            //复制数组引用
//            dup();
//            //把索引放进操作栈中
//            push(i);
//            //将局部变量加载到操作栈顶
//            loadArg(i);
//            //装箱
//            box(argumentTypeArray[i]);
//            //消耗arrayref(数组引用), index(索引), value(局部变量)
//            //从操作栈中弹出赋值到数组的局部变量中
//            arrayStore(OBJECT_TYPE);
//        }
//    }

    protected void storeArgArray(){
        for (int i = 0; i < argumentTypeArray.length; i++) {
            //复制一个参数组的引用
            dup();
            //将索引index放进操作栈中
            push(i);
            //将一个局部变量加载到操纵栈 此时消耗一个数组引用和索引
            //arrayref, index →
            arrayLoad(ASM_TYPE_OBJECT);
            //拆箱
            unbox(argumentTypeArray[i]);
            //弹出操作栈中的值赋予索引的局部变量
            storeArg(i);
        }
    }

}
