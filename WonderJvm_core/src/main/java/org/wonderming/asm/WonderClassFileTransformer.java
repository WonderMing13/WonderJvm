package org.wonderming.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.wonderming.asm.EventWeaver;
import org.wonderming.manager.EventListener;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;



/**
 * @author wangdeming
 **/
public class WonderClassFileTransformer implements ClassFileTransformer {

    private EventListener eventListener;

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer){
        final ClassReader classReader = new ClassReader(classfileBuffer);
        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classReader.accept(new EventWeaver(classWriter),ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
