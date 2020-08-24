package org.wonderming.manager.structure;

import org.wonderming.manager.CoreClassStructure;
import org.wonderming.utils.LazyLoadUtil;

import java.lang.annotation.Inherited;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author wangdeming
 **/
public abstract class BaseClassStructure implements CoreClassStructure {

    private final LazyLoadUtil.BaseLazyLoad<HashSet<CoreClassStructure>> familyInterfaceClassStructuresLazyLoad = new LazyLoadUtil.BaseLazyLoad<HashSet<CoreClassStructure>>() {
        @Override
        public HashSet<CoreClassStructure> initValue() {
            final HashSet<CoreClassStructure> familyInterfaceClassStructures = new HashSet<>();
            for (CoreClassStructure coreClassStructure:getInterfaceClassStructures()) {
                //1.添加自己
                familyInterfaceClassStructures.add(coreClassStructure);
                //2.添加接口继承
                familyInterfaceClassStructures.addAll(coreClassStructure.getFamilyInterfaceClassStructures());
            }

            for (CoreClassStructure coreClassStructure:getFamilySuperClassStructures()) {
                //3.添加父类接口
                familyInterfaceClassStructures.add(coreClassStructure);
                //4.添加父类接口继承
                familyInterfaceClassStructures.addAll(coreClassStructure.getFamilyInterfaceClassStructures());
            }
            return familyInterfaceClassStructures;
        }
    };

    private final LazyLoadUtil.BaseLazyLoad<LinkedHashSet<CoreClassStructure>> familySuperClassStructuresLazyLoad = new LazyLoadUtil.BaseLazyLoad<LinkedHashSet<CoreClassStructure>>() {
        @Override
        public LinkedHashSet<CoreClassStructure> initValue() {
            final LinkedHashSet<CoreClassStructure> familySuperClassStructures = new LinkedHashSet<>();
            //1.添加父类
            familySuperClassStructures.add(getSuperClassStructure());
            //2.添加爷爷类们
            familySuperClassStructures.addAll(getSuperClassStructure().getFamilySuperClassStructures());
            return familySuperClassStructures;
        }
    };

    private final LazyLoadUtil.BaseLazyLoad<Set<CoreClassStructure>> familyAnnotationTypeClassStructuresLazyLoad = new LazyLoadUtil.BaseLazyLoad<Set<CoreClassStructure>>() {
        @Override
        public Set<CoreClassStructure> initValue() {
            final Set<CoreClassStructure> familyAnnotationTypeClassStructures = new HashSet<CoreClassStructure>(getAnnotationTypeClassStructures());
            for (CoreClassStructure coreClassStructure:getFamilyTypeClassStructures()) {
                familyAnnotationTypeClassStructures.addAll(
                        filterAnnotationTypeClassStructure(coreClassStructure.getFamilyAnnotationTypeClassStructures()));
            }
            return familyAnnotationTypeClassStructures;
        }
    };

    private final LazyLoadUtil.BaseLazyLoad<HashSet<CoreClassStructure>> familyTypeClassStructuresLazyLoad = new LazyLoadUtil.BaseLazyLoad<HashSet<CoreClassStructure>>() {
        @Override
        public HashSet<CoreClassStructure> initValue() {
            final HashSet<CoreClassStructure> familyTypeClassStructures = new HashSet<>();
            // 注入家族类&家族类所声明的家族接口
            for (CoreClassStructure coreClassStructure :getFamilySuperClassStructures()) {
                familyTypeClassStructures.add(coreClassStructure);
                familyTypeClassStructures.addAll(coreClassStructure.getFamilyInterfaceClassStructures());
            }
            //注入家族接口
            for (final CoreClassStructure familyInterfaceClassStructure : getFamilyInterfaceClassStructures()) {
                familyTypeClassStructures.add(familyInterfaceClassStructure);
                familyTypeClassStructures.addAll(familyInterfaceClassStructure.getFamilyInterfaceClassStructures());
            }
            return familyTypeClassStructures;
        }
    };

    private Set<CoreClassStructure> filterAnnotationTypeClassStructure(Set<CoreClassStructure> familyAnnotationTypeClassStructures){
        final Set<CoreClassStructure> filterAnnotationTypeClassStructures = new HashSet<>();
        for (CoreClassStructure coreClassStructure:familyAnnotationTypeClassStructures) {
            if (isInheritedAnnotationType(coreClassStructure)){
                filterAnnotationTypeClassStructures.add(coreClassStructure);
            }
        }
        return filterAnnotationTypeClassStructures;
    }

    private boolean isInheritedAnnotationType(CoreClassStructure coreClassStructure){
        if (!coreClassStructure.getAccess().isAnnotation()){
            return false;
        }
        for (CoreClassStructure annotationTypeClassStructure:coreClassStructure.getAnnotationTypeClassStructures()) {
            if (Inherited.class.getName().equals(annotationTypeClassStructure.getJavaClassName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public LinkedHashSet<CoreClassStructure> getFamilySuperClassStructures() {
        return familySuperClassStructuresLazyLoad.get();
    }

    @Override
    public HashSet<CoreClassStructure> getFamilyInterfaceClassStructures() {
        return familyInterfaceClassStructuresLazyLoad.get();
    }

    @Override
    public Set<CoreClassStructure> getFamilyAnnotationTypeClassStructures() {
        return familyAnnotationTypeClassStructuresLazyLoad.get();
    }

    @Override
    public HashSet<CoreClassStructure> getFamilyTypeClassStructures() {
        return familyTypeClassStructuresLazyLoad.get();
    }

    @Override
    public int hashCode() {
        return getJavaClassName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CoreClassStructure
                && getJavaClassName().equals(((CoreClassStructure) obj).getJavaClassName());
    }
}
