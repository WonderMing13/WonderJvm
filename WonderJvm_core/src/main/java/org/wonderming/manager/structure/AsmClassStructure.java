package org.wonderming.manager.structure;

import org.wonderming.manager.CoreAccess;
import org.wonderming.manager.CoreClassStructure;

import java.util.List;

/**
 * @author wangdeming
 **/
public class AsmClassStructure extends BaseClassStructure {

    @Override
    public CoreAccess getAccess() {
        return null;
    }

    @Override
    public String getJavaClassName() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public List<MethodStructure> getMethodStructure() {
        return null;
    }

    @Override
    public CoreClassStructure getSuperClassStructure() {
        return null;
    }

    @Override
    public List<CoreClassStructure> getInterfaceClassStructures() {
        return null;
    }

    @Override
    public List<CoreClassStructure> getAnnotationTypeClassStructures() {
        return null;
    }
}
