package org.wonderming.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wonderming.Spy;
import org.wonderming.model.MatchingResult;

/**
 * @author wangdeming
 **/
public class EventWeaver extends ClassVisitor implements Opcodes,AsmMethod {

    private final Logger logger = LoggerFactory.getLogger(EventWeaver.class);

    private final MatchingResult matchingResult;

    private final int targetClassLoaderId;

    private final int listenerId;

    private final String namespace;

    public EventWeaver(ClassVisitor cv, MatchingResult matchingResult, int targetClassLoaderId, int listenerId, String namespace) {
        super(ASM7, cv);
        this.matchingResult = matchingResult;
        this.targetClassLoaderId = targetClassLoaderId;
        this.listenerId = listenerId;
        this.namespace = namespace;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        final String targetClassName = matchingResult.getFilterClassList().get(0).getPattern();
        final String targetMethodName = matchingResult.getMethodStructureList().get(0).getName();
        //如果没有匹配上则跳过
        if (!targetMethodName.equals(name)){
            return mv;
        }
        return new EventMethod(new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions),access,name,desc){
            private final Label beginLabel = new Label();

            private void processControl(){
                final Label finishLabel = new Label();
                final Label returnLabel = new Label();
                final Label throwsLabel = new Label();
                //此时操作栈只剩一个返回的结果 再复制一份栈顶的数据 因为之后GETFIELD指令会需要消耗一份引用
                dup();
                //objectref →
                visitFieldInsn(GETFIELD,ASM_TYPE_SPY_RESULT,"state",ASM_TYPE_INT);
                //复制一份返回的state
                dup();
                push(Spy.SpyOfResult.SPY_RESULT_STATE_RETURN);
                //value → 如果是返回
                ifICmp(EQ,returnLabel);
                push(Spy.SpyOfResult.SPY_RESULT_STATE_THROWS);
                ifICmp(EQ,throwsLabel);
                goTo(finishLabel);
                mark(returnLabel);
                //弹出state
                pop();
                visitFieldInsn(GETFIELD,ASM_TYPE_SPY_RESULT,"response",ASM_TYPE_OBJECT);
                checkCastReturn(Type.getReturnType(desc));
                goTo(finishLabel);
                mark(throwsLabel);
                visitFieldInsn(GETFIELD,ASM_TYPE_SPY_RESULT,"response",ASM_TYPE_OBJECT);
                checkCast(ASM_TYPE_THROWABLE);
                throwException();
                mark(finishLabel);
                //弹出结果
                pop();
            }

            @Override
            protected void onMethodEnter() {
                mark(beginLabel);
                //加载方法参数 dup()复制一遍数组引用 IASTORE则需要消耗一个数组引用
                //push是将value放进操作栈中 STORE是将value放进本地数值表中 LOAD则是将value加载复制给变量
                loadArgArray();
                //此时操作栈中只有一个数组引用在复制一份到栈顶
                //目标方法参数
                dup();
                //目标ClassLoader
                push(targetClassLoaderId);
                //监听器ID
                push(listenerId);
                //命名空间
                push(namespace);
                //目标类名
                push(targetClassName);
                //目标方法名称
                push(name);
                //目标方法描述
                push(desc);
                //目标对象
                loadThisOrPushNullIfIsStatic();
                //拦截static方法后会返回该结果值在栈顶 同时操作栈弹出所需的参数
                invokeStatic(ASM_TYPE_SPY,ASM_SPY_METHOD_CALL_BEFORE);
                //将参数和返回结果进行互换 此时栈顶为参数
                swap();
                //将参数保存到局部变量
                storeArgArray();
                //弹出该参数组的引用
                pop();
                processControl();
            }
        };
    }

}
