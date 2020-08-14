package org.wonderming.manager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wonderming.CoreModuleEventWatcher;
import org.wonderming.asm.WonderClassFileTransformer;
import org.wonderming.classloader.ModuleJarClassLoader;
import org.wonderming.manager.CoreModuleLoadedClassManager;
import org.wonderming.model.ConditionBuilder;
import org.wonderming.model.FilterModel;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;
import java.util.Set;

/**
 * @author wangdeming
 **/
public class DefaultCoreModuleEventWatcher implements CoreModuleEventWatcher {

    private final Logger logger = LoggerFactory.getLogger(DefaultCoreModuleEventWatcher.class);

    private final CoreModuleLoadedClassManager coreModuleLoadedClassManager;

    private final Instrumentation inst;

    public DefaultCoreModuleEventWatcher(CoreModuleLoadedClassManager coreModuleLoadedClassManager, Instrumentation inst) {
        this.coreModuleLoadedClassManager = coreModuleLoadedClassManager;
        this.inst = inst;
    }

    @Override
    public void watch(List<ConditionBuilder.BuildingForClass> buildingForClassList) {
        final Set<Class<?>> allClassLoadedSet = coreModuleLoadedClassManager.getAllClassLoadedSet();

        final FilterModel filterModel = new FilterModel();
        filterModel.setBuildingForClassList(buildingForClassList);
        filterModel.setAllClassLoadedSet(allClassLoadedSet);

        final WonderClassFileTransformer wonderClassFileTransformer = new WonderClassFileTransformer(filterModel);
        inst.addTransformer(wonderClassFileTransformer, true);

        final List<Class<?>> waitingReTransformList = coreModuleLoadedClassManager.findForReTransform(buildingForClassList);
        logger.info("waiting ReTransform Class num:{}", waitingReTransformList.size());

        try {
            for (Class<?> waitingClass : waitingReTransformList) {
                inst.retransformClasses(waitingClass);
            }
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }


    }
}
