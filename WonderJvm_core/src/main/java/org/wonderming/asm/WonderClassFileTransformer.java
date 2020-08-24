package org.wonderming.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wonderming.manager.filter.DefaultClassFilter;
import org.wonderming.manager.filter.DefaultMethodFilter;
import org.wonderming.manager.filter.FilterChain;
import org.wonderming.manager.structure.AsmClassStructure;
import org.wonderming.model.AdviceListener;
import org.wonderming.model.FilterModel;
import org.wonderming.manager.CoreClassStructure;
import org.wonderming.manager.structure.JdkClassStructure;
import org.wonderming.model.MatchingResult;
import org.wonderming.utils.AsmUtils;
import org.wonderming.utils.ObjectIdUtil;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;


/**
 * @author wangdeming
 **/
public class WonderClassFileTransformer implements ClassFileTransformer {

    private final Logger logger = LoggerFactory.getLogger(WonderClassFileTransformer.class);

    private final FilterModel filterModel;

    private final AdviceListener adviceListener;

    private final String namespace;

    public WonderClassFileTransformer(FilterModel filterModel, AdviceListener adviceListener, String namespace) {
        this.filterModel = filterModel;
        this.adviceListener = adviceListener;
        this.namespace = namespace;
    }

    /**
     * 创建ClassWriter for asm
     *
     * @param cr ClassReader
     * @return ClassWriter
     */
    private ClassWriter createClassWriter(final ClassLoader targetClassLoader,
                                          final ClassReader cr) {
        return new ClassWriter(cr, COMPUTE_FRAMES | COMPUTE_MAXS) {

            /*
             * 注意，为了自动计算帧的大小，有时必须计算两个类共同的父类。
             * 缺省情况下，ClassWriter将会在getCommonSuperClass方法中计算这些，通过在加载这两个类进入虚拟机时，使用反射API来计算。
             * 但是，如果你将要生成的几个类相互之间引用，这将会带来问题，因为引用的类可能还不存在。
             * 在这种情况下，你可以重写getCommonSuperClass方法来解决这个问题。
             *
             * 通过重写 getCommonSuperClass() 方法，更正获取ClassLoader的方式，改成使用指定ClassLoader的方式进行。
             * 规避了原有代码采用Object.class.getClassLoader()的方式
             */
            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                return AsmUtils.getCommonSuperClass(type1, type2, targetClassLoader);
            }

        };
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer){
        final ClassReader classReader = new ClassReader(classfileBuffer);
        final ClassWriter classWriter = createClassWriter(loader,classReader);
        CoreClassStructure coreClassStructure = ((null != classBeingRedefined) ?
                                                new JdkClassStructure(classBeingRedefined)
                                              : new AsmClassStructure(classfileBuffer,loader));
        logger.info("create coreClassStructure's JavaClassName is {}",coreClassStructure.getJavaClassName());
        filterModel.setCoreClassStructure(coreClassStructure);
        final FilterChain filterChain = new FilterChain();
        final MatchingResult matchingResult = filterChain.addFilter(new DefaultClassFilter()).addFilter(new DefaultMethodFilter()).build(filterModel, filterChain);
        classReader.accept(new EventWeaver(
                        classWriter,
                        matchingResult,
                        ObjectIdUtil.OBJECT_ID_UTIL.put(loader),
                        ObjectIdUtil.OBJECT_ID_UTIL.put(adviceListener),
                        namespace),
                ClassReader.EXPAND_FRAMES);
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
