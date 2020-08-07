package org.wonderming.manager.structure;

import org.wonderming.manager.CoreAccess;
import org.wonderming.manager.CoreClassStructure;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wangdeming
 **/
public abstract class BaseClassStructure implements CoreClassStructure {

    @Override
    public LinkedHashSet<CoreClassStructure> getFamilySuperClassStructures() {
        return null;
    }

    @Override
    public List<CoreClassStructure> getInterfaceClassStructures() {
        return null;
    }

    @Override
    public HashSet<CoreClassStructure> getFamilyInterfaceClassStructures() {
        return null;
    }

    @Override
    public List<CoreClassStructure> getAnnotationTypeClassStructures() {
        return null;
    }

    @Override
    public Set<CoreClassStructure> getFamilyAnnotationTypeClassStructures() {
        return null;
    }

    @Override
    public HashSet<CoreClassStructure> getFamilyTypeClassStructures() {
        return null;
    }
}
