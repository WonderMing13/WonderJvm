package org.wonderming.manager.structure;

import org.wonderming.manager.CoreAccess;
import org.wonderming.manager.CoreClassStructure;
import org.wonderming.utils.LazyLoadUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author wangdeming
 **/
public class JdkClassStructure extends BaseClassStructure {

    private final Class<?> clazz;

    private String javaClassName;

    public JdkClassStructure(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public CoreAccess getAccess() {
        return new JdkModifierAccessClass(clazz);
    }

    @Override
    public String getJavaClassName() {
        return null != javaClassName ? javaClassName : getJavaClassName(clazz);
    }

    @Override
    public ClassLoader getClassLoader() {
        return clazz.getClassLoader();
    }

    @Override
    public List<MethodStructure> getMethodStructure() {
        return methodStructureListLazyLoad.get();
    }

    @Override
    public CoreClassStructure getSuperClassStructure() {
        return Object.class.equals(clazz.getSuperclass()) ? null : newInstance(clazz);
    }

    @Override
    public List<CoreClassStructure> getInterfaceClassStructures() {
        return newInstanceList(clazz.getInterfaces());
    }

    @Override
    public List<CoreClassStructure> getAnnotationTypeClassStructures() {
        return annotationTypeClassStructuresLazyLoad.get();
    }

    /**
     * 获取Jdk类的访问修饰符
     */
    public static class JdkModifierAccessClass extends BaseModifierAccess {

        private final Class<?> clazz;

        JdkModifierAccessClass(Class<?> clazz) {
            super(clazz.getModifiers());
            this.clazz = clazz;
        }

        @Override
        public boolean isEnum() {
            return clazz.isEnum();
        }

        @Override
        public boolean isAnnotation() {
            return clazz.isAnnotation();
        }
    }

    /**
     * 获取Method类的访问修饰符
     */
    public static class JdkModifierAccessMethod extends BaseModifierAccess {

        JdkModifierAccessMethod(Method method){
            super(method.getModifiers());
        }

        JdkModifierAccessMethod(Constructor<?> constructor){
            super(constructor.getModifiers());
        }

        @Override
        public boolean isEnum() {
            return false;
        }

        @Override
        public boolean isAnnotation() {
            return false;
        }
    }

    /**
     * 懒加载方法的类信息
     */
    private final LazyLoadUtil.BaseLazyLoad<List<MethodStructure>> methodStructureListLazyLoad = new LazyLoadUtil.BaseLazyLoad<List<MethodStructure>>() {
        @Override
        public List<MethodStructure> initValue() {
            List<MethodStructure> methodStructureList = new ArrayList<>();
            for (Method method:clazz.getDeclaredMethods()) {
                methodStructureList.add(newMethodStructure(method));
            }
            for (Constructor<?> constructor:clazz.getDeclaredConstructors()) {
                methodStructureList.add(newMethodStructure(constructor));
            }
            //只可读状态不可修改
            return Collections.unmodifiableList(methodStructureList);
        }
    };

    /**
     * 注解类信息懒加载
     */
    private final LazyLoadUtil.BaseLazyLoad<List<CoreClassStructure>> annotationTypeClassStructuresLazyLoad = new LazyLoadUtil.BaseLazyLoad<List<CoreClassStructure>>() {
        @Override
        public List<CoreClassStructure> initValue() {
            return Collections.unmodifiableList(newInstanceList(getAnnotationArray(clazz.getAnnotations())));
        }
    };

    /**
     * 构建方法的方法信息
     * @param method Method
     * @return MethodStructure
     */
    public MethodStructure newMethodStructure(Method method){
        return new MethodStructure(
                new JdkModifierAccessMethod(method),
                method.getName(),
                newInstance(method.getDeclaringClass()),
                newInstance(method.getReturnType()),
                newInstanceList(method.getParameterTypes()),
                newInstanceList(method.getExceptionTypes()),
                newInstanceList(getAnnotationArray(method.getAnnotations()))
        );
    }

    /**
     * 构建方法的构造器
     * @param constructor 构造方法
     * @return MethodStructure
     */
    public MethodStructure newMethodStructure(Constructor<?> constructor){
        return new MethodStructure(
                new JdkModifierAccessMethod(constructor),
                "<init>",
                this,
                this,
                newInstanceList(constructor.getParameterTypes()),
                newInstanceList(constructor.getExceptionTypes()),
                newInstanceList(getAnnotationArray(constructor.getAnnotations()))
        );
    }

    /**
     * 获取annotation数组
     * @param annotationArray Annotation[]
     * @return Class<?>[] Class数组
     */
    public Class<?>[] getAnnotationArray(Annotation[] annotationArray){
        List<Class<?>> annotationTypes = new ArrayList<>();
        for (Annotation annotation:annotationArray) {
            if (annotation.getClass().isAnnotation()){
                annotationTypes.add(annotation.getClass());
            }
            for (Class<?> annotationInterfaceClass:annotation.getClass().getInterfaces()) {
                if (annotationInterfaceClass.isAnnotation()){
                    annotationTypes.add(annotationInterfaceClass);
                }
            }
        }
        return annotationTypes.toArray(new Class[0]);
    }

    /**
     * 获取Java的全类名
     * @param clazz Class<?> 被拦截的类
     * @return String
     */
    private String getJavaClassName(Class<?> clazz) {
        if (clazz.isArray()) {
            return getJavaClassName(clazz.getComponentType()) + "[]";
        }
        return clazz.getName();
    }

    /**
     * 包装成对象
     * @param clazz Class<?>
     * @return CoreClassStructure
     */
    public CoreClassStructure newInstance(Class<?> clazz){
        return null == clazz ? null : new JdkClassStructure(clazz);
    }

    /**
     * 根据接口包装成对象
     * @param clazz Class<?>[]
     * @return List<CoreClassStructure>
     */
    public List<CoreClassStructure> newInstanceList(Class<?>[] clazz){
        List<CoreClassStructure> coreClassStructureList = new ArrayList<>();
        for (Class<?> c:clazz) {
            final CoreClassStructure coreClassStructure = newInstance(c);
            if (coreClassStructure != null){
                coreClassStructureList.add(coreClassStructure);
            }
        }
        return coreClassStructureList;
    }
}
