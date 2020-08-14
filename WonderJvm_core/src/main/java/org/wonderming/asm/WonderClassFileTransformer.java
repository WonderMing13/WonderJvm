package org.wonderming.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.wonderming.manager.filter.DefaultClassFilter;
import org.wonderming.manager.filter.DefaultMethodFilter;
import org.wonderming.manager.filter.FilterChain;
import org.wonderming.model.FilterModel;
import org.wonderming.manager.CoreClassStructure;
import org.wonderming.manager.structure.JdkClassStructure;
import org.wonderming.model.MatchingResult;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;



/**
 * @author wangdeming
 **/
public class WonderClassFileTransformer implements ClassFileTransformer {

    private final FilterModel filterModel;

    public WonderClassFileTransformer(FilterModel filterModel) {
        this.filterModel = filterModel;
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer){
        final ClassReader classReader = new ClassReader(classfileBuffer);
        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        CoreClassStructure coreClassStructure = new JdkClassStructure(classBeingRedefined);
        filterModel.setCoreClassStructure(coreClassStructure);

        final FilterChain filterChain = new FilterChain();
        final MatchingResult matchingResult = filterChain.addFilter(new DefaultClassFilter()).addFilter(new DefaultMethodFilter()).build(filterModel, filterChain);


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
