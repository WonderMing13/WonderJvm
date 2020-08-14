package org.wonderming.manager.impl;

import org.wonderming.manager.CoreModuleLoadedClassManager;
import org.wonderming.model.ConditionBuilder;

import java.lang.instrument.Instrumentation;
import java.util.*;

/**
 * @author wangdeming
 **/
public class DefaultCoreLoadedClassSource implements CoreModuleLoadedClassManager {

    private final Instrumentation inst;

    public DefaultCoreLoadedClassSource(Instrumentation inst) {
        this.inst = inst;
    }

    @Override
    public Set<Class<?>> getAllClassLoadedSet() {
        Set<Class<?>> allClassLoadedSets = new HashSet<>();
        for (Class<?> a: inst.getAllLoadedClasses()) {
            allClassLoadedSets.add(a);
        }
        return allClassLoadedSets;
    }

    @Override
    public List<Class<?>> findForReTransform(List<ConditionBuilder.BuildingForClass> buildingForClassList) {
        final List<Class<?>> reTransformClass = new ArrayList<>();
        buildingForClassList.forEach(buildingForClass -> {
            for (Class<?> instClass : getAllClassLoadedSet()) {
                if (buildingForClass.getPattern().equals(instClass.getName())){
                    reTransformClass.add(instClass);
                }
            }
        });
        return reTransformClass;
    }
}
