package org.wonderming.manager.structure;

import org.wonderming.manager.CoreAccess;
import org.wonderming.manager.CoreClassStructure;
import org.wonderming.manager.structure.BaseClassStructure;

import java.util.*;

/**
 * @author wangdeming
 **/
public class JdkClassStructure extends BaseClassStructure {

    private final Class<?> clazz;

    public JdkClassStructure(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public CoreAccess getAccess() {
        return new JdkModifierAccess(clazz);
    }

    @Override
    public String getJavaClassName() {
        return clazz.getName();
    }

    @Override
    public ClassLoader getClassLoader() {
        return clazz.getClassLoader();
    }

    @Override
    public List<MethodStructure> getMethodStructure() {
        return null;
    }

    @Override
    public CoreClassStructure getSuperClassStructure() {
        return Object.class.equals(clazz.getSuperclass()) ? null : newInstance(clazz);
    }


    @Override
    public List<CoreClassStructure> getInterfaceClassStructures() {
        return newInstanceList(clazz.getInterfaces());
    }


    public class JdkModifierAccess extends BaseModifierAccess {
        private final Class<?> clazz;

        JdkModifierAccess(Class<?> clazz) {
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

    public CoreClassStructure newInstance(Class<?> clazz){
        return null == clazz ? null : new JdkClassStructure(clazz);
    }

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
