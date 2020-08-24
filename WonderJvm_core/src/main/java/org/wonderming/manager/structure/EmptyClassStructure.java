package org.wonderming.manager.structure;

import org.wonderming.manager.CoreAccess;
import org.wonderming.manager.CoreClassStructure;
import org.wonderming.manager.access.AsmModifierAccess;

import java.util.*;

/**
 * @author wangdeming
 **/
public class EmptyClassStructure implements CoreClassStructure {

    @Override
    public CoreAccess getAccess() {
        return new AsmModifierAccess(0);
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
        return Collections.emptyList();
    }

    @Override
    public CoreClassStructure getSuperClassStructure() {
        return null;
    }

    @Override
    public Set<CoreClassStructure> getFamilySuperClassStructures() {
        return Collections.emptySet();
    }

    @Override
    public List<CoreClassStructure> getInterfaceClassStructures() {
        return Collections.emptyList();
    }

    @Override
    public Set<CoreClassStructure> getFamilyInterfaceClassStructures() {
        return Collections.emptySet();
    }

    @Override
    public List<CoreClassStructure> getAnnotationTypeClassStructures() {
        return Collections.emptyList();
    }

    @Override
    public Set<CoreClassStructure> getFamilyAnnotationTypeClassStructures() {
        return Collections.emptySet();
    }

    @Override
    public Set<CoreClassStructure> getFamilyTypeClassStructures() {
        return Collections.emptySet();
    }
}
