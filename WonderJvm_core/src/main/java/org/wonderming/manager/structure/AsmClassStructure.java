package org.wonderming.manager.structure;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wonderming.manager.CoreAccess;
import org.wonderming.manager.CoreClassStructure;
import org.wonderming.manager.access.AsmModifierAccess;
import org.wonderming.utils.LazyLoadUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.ASM7;
import static org.wonderming.manager.structure.AsmClassStructure.PrimitiveClassStructure.mappingPrimitiveByJavaClassName;

/**
 * @author wangdeming
 **/
public class AsmClassStructure extends BaseClassStructure {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClassLoader classLoader;

    private final ClassReader classReader;

    private final CoreAccess coreAccess;

    public AsmClassStructure(InputStream inputStream,ClassLoader classLoader) throws IOException {
        this(IOUtils.toByteArray(inputStream),classLoader);
    }

    public AsmClassStructure(byte[] classByteArray,ClassLoader classLoader){
        this.classReader = new ClassReader(classByteArray);
        this.classLoader = classLoader;
        this.coreAccess = findCoreAccess();
    }


    @Override
    public CoreAccess getAccess() {
        return coreAccess;
    }

    @Override
    public String getJavaClassName() {
        return changeInternalClassName(this.classReader.getClassName());
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public List<MethodStructure> getMethodStructure() {
        return methodStructureLazyLoad.get();
    }

    @Override
    public CoreClassStructure getSuperClassStructure() {
        return superClassStructureLazyLoad.get();
    }

    @Override
    public List<CoreClassStructure> getInterfaceClassStructures() {
        return interfaceClassStructuresLazyLoad.get();
    }

    @Override
    public List<CoreClassStructure> getAnnotationTypeClassStructures() {
        return annotationTypeClassStructuresLazyLoad.get();
    }

    private final LazyLoadUtil.BaseLazyLoad<CoreClassStructure> superClassStructureLazyLoad = new LazyLoadUtil.BaseLazyLoad<CoreClassStructure>() {
        @Override
        public CoreClassStructure initValue() {
            final String superName = classReader.getSuperName();
            if (StringUtils.equals("java/lang/Object",superName)){
                return null;
            }
            return newInstance(changeInternalClassName(superName));
        }
    };

    private final LazyLoadUtil.BaseLazyLoad<List<CoreClassStructure>> interfaceClassStructuresLazyLoad = new LazyLoadUtil.BaseLazyLoad<List<CoreClassStructure>>() {
        @Override
        public List<CoreClassStructure> initValue() {
            return newInstanceList(classReader.getInterfaces());
        }
    };

    private final LazyLoadUtil.BaseLazyLoad<List<CoreClassStructure>> annotationTypeClassStructuresLazyLoad = new LazyLoadUtil.BaseLazyLoad<List<CoreClassStructure>>() {
        @Override
        public List<CoreClassStructure> initValue() {
            List<CoreClassStructure> annotationTypeClassStructures = new ArrayList<>();
            classReader.accept(new ClassVisitor(ASM7) {
                @Override
                public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                    if (visible){
                        final CoreClassStructure coreClassStructure = newInstance(Type.getType(desc).getClassName());
                        if (null != coreClassStructure){
                            annotationTypeClassStructures.add(coreClassStructure);
                        }
                    }
                    return super.visitAnnotation(desc, visible);
                }
            },ASM7);
            return annotationTypeClassStructures;
        }
    };

    private final LazyLoadUtil.BaseLazyLoad<List<MethodStructure>> methodStructureLazyLoad = new LazyLoadUtil.BaseLazyLoad<List<MethodStructure>>() {
        @Override
        public List<MethodStructure> initValue() {
            final List<MethodStructure> methodStructureList = new ArrayList<>();
            classReader.accept(new ClassVisitor(ASM7) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (StringUtils.equals("<clinit>", name)) {
                        return super.visitMethod(access, name, desc, signature, exceptions);
                    }
                    return new MethodVisitor(ASM7,super.visitMethod(access,name,desc,signature,exceptions)) {

                        private final Type methodType = Type.getMethodType(desc);

                        private final List<CoreClassStructure> annotationTypeClassStructures = new ArrayList<>();

                        private CoreClassStructure getReturnTypeClassStructure(){
                            if ("<init>".equals(name)){
                                //指的是AsmClassStructure这个类
                                return AsmClassStructure.this;
                            }else {
                                final Type returnType = methodType.getReturnType();
                                return newInstance(returnType.getClassName());
                            }
                        }

                        private List<CoreClassStructure> getParameterTypeClassStructures(Type[] typeArray){
                            List<String> javaClassNameList = new ArrayList<>();
                            if (null != typeArray){
                                for (Type type : typeArray) {
                                    javaClassNameList.add(type.getClassName());
                                }
                            }
                            return newInstanceList(javaClassNameList.toArray(new String[0]));
                        }

                        @Override
                        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                            if (visible){
                                final CoreClassStructure annotationTypeClassStructure = newInstance(Type.getType(desc).getClassName());
                                if (null != annotationTypeClassStructure){
                                    annotationTypeClassStructures.add(annotationTypeClassStructure);
                                }
                            }
                            return super.visitAnnotation(desc, visible);
                        }

                        @Override
                        public void visitEnd() {
                            super.visitEnd();
                            final MethodStructure methodStructure = new MethodStructure(
                                    new AsmModifierAccess(access),
                                    name,
                                    AsmClassStructure.this,
                                    getReturnTypeClassStructure(),
                                    getParameterTypeClassStructures(methodType.getArgumentTypes()),
                                    newInstanceList(exceptions),
                                    annotationTypeClassStructures
                            );
                            methodStructureList.add(methodStructure);
                        }
                    };
                }
            },ASM7);
            return methodStructureList;
        }
    };

    /**
     * 基础类型实例
     */
    public static class PrimitiveClassStructure extends EmptyClassStructure{

        private final Primitive primitive;

        public PrimitiveClassStructure(Primitive primitive) {
            this.primitive = primitive;
        }

        public enum Primitive {
            BOOLEAN("boolean",boolean.class),
            INT("int",int.class),
            CHAR("char",char.class),
            BYTE("byte",byte.class),
            SHORT("short",short.class),
            LONG("long",long.class),
            FLOAT("float",float.class),
            DOUBLE("double",double.class),
            VOID("void",void.class);

            private final String type;

            private final CoreAccess coreAccess;

            Primitive(String type, Class<?> clazz) {
                this.type = type;
                this.coreAccess = new JdkClassStructure.JdkModifierAccessClass(clazz);
            }
        }

        @Override
        public CoreAccess getAccess() {
            return primitive.coreAccess;
        }

        @Override
        public String getJavaClassName() {
            return primitive.type;
        }

        public static Primitive mappingPrimitiveByJavaClassName(final String javaClassName){
            for (Primitive primitive : Primitive.values()) {
                if (primitive.type.equals(javaClassName)){
                    return primitive;
                }
            }
            return null;
        }
    }

    /**
     * 构建数组实例
     */
    public static class ArrayClassStructure extends EmptyClassStructure{
        private final CoreClassStructure elementClassStructure;

        public ArrayClassStructure(CoreClassStructure elementClassStructure) {
            this.elementClassStructure = elementClassStructure;
        }

        @Override
        public String getJavaClassName() {
            return elementClassStructure.getJavaClassName() + "[]";
        }
    }

    private CoreAccess findCoreAccess(){
        final AtomicInteger accessRef = new AtomicInteger(this.classReader.getAccess());
        final String internalClassName = this.classReader.getClassName();
        this.classReader.accept(new ClassVisitor(ASM7) {
            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
                if (StringUtils.equals(name,internalClassName)){
                    accessRef.set(access);
                }
            }
        },ASM7);
        return new AsmModifierAccess(accessRef.get());
    }

    private boolean isBootstrapClassLoader(){
        return null == classLoader;
    }

    private InputStream getResourceAsStream(String resourceName){
        return isBootstrapClassLoader() ?
                //path 不以’/'开头时默认是从此类所在的包下取资源，以’/'开头则是从ClassPath根下获取。其只是通过path构造一个绝对路径，最终还是由ClassLoader获取资源。
                Object.class.getResourceAsStream("/" + resourceName)
                //默认则是从ClassPath根下获取，path不能以’/'开头，最终是由ClassLoader获取资源
                : classLoader.getResourceAsStream(resourceName);
    }

    private final static Map<List<Object>,CoreClassStructure> CACHE_MAP = new ConcurrentHashMap<>();

    private CoreClassStructure newInstance(String javaClassName){
        //数组类型
        if (javaClassName.endsWith("[]")){
            return new ArrayClassStructure(newInstance(javaClassName.substring(0,javaClassName.length() - 2)));
        }
        final PrimitiveClassStructure.Primitive primitive = mappingPrimitiveByJavaClassName(javaClassName);
        if (null != primitive){
            return new PrimitiveClassStructure(primitive);
        }
        //如果存在则返回coreClassStructure
        final CoreClassStructure coreClassStructure = CACHE_MAP.computeIfPresent(Arrays.asList(classLoader, javaClassName), (k, v) -> v);
        if (null != coreClassStructure){
            return coreClassStructure;
        }else {
            final InputStream resourceAsStream = getResourceAsStream(internalClassNameToResourceName(changeInternalClassName(javaClassName)));
            if (null != resourceAsStream){
                try {
                    final AsmClassStructure asmClassStructure = new AsmClassStructure(resourceAsStream, classLoader);
                    CACHE_MAP.put(Arrays.asList(classLoader,javaClassName),asmClassStructure);
                    return asmClassStructure;
                } catch (IOException e) {
                    logger.warn("new instance class structure by using ASM failed, will return null. class={};loader={};",
                            javaClassName, classLoader, e.getCause());
                    CACHE_MAP.remove(Arrays.asList(classLoader,javaClassName));
                }finally {
                    IOUtils.closeQuietly(resourceAsStream);
                }
            }
        }
        return null;
    }

    private List<CoreClassStructure> newInstanceList(String[] javaClassNameArray){
        List<CoreClassStructure> coreClassStructureList = new ArrayList<>();
        for (String javaClassName:javaClassNameArray) {
            final CoreClassStructure coreClassStructure = newInstance(javaClassName);
            if (null != coreClassStructure){
                coreClassStructureList.add(coreClassStructure);
            }
        }
        return coreClassStructureList;
    }

    public static String changeInternalClassName(String javaClassName){
        if (StringUtils.isEmpty(javaClassName)){
            return javaClassName;
        }
        return javaClassName.replace('.','/');
    }

    private String internalClassNameToResourceName(String internalClassName){
        return internalClassName + ".class";
    }


    @Override
    public String toString(){
        return "AsmClassStructure{" + "javaClassName='" + getJavaClassName() + '\'' + '}';
    }
}
