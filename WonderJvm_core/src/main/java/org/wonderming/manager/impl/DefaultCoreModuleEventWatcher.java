package org.wonderming.manager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wonderming.CoreModuleEventWatcher;
import org.wonderming.asm.WonderClassFileTransformer;
import org.wonderming.manager.CoreModuleLoadedClassManager;
import org.wonderming.model.AdviceListener;
import org.wonderming.model.ConditionBuilder;
import org.wonderming.model.CoreModuleModel;
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

    private final String namespace;

    private final CoreModuleModel coreModuleModel;

    public DefaultCoreModuleEventWatcher(CoreModuleLoadedClassManager coreModuleLoadedClassManager, Instrumentation inst, String namespace, CoreModuleModel coreModuleModel) {
        this.coreModuleLoadedClassManager = coreModuleLoadedClassManager;
        this.inst = inst;
        this.namespace = namespace;
        this.coreModuleModel = coreModuleModel;
    }

    @Override
    public void watch(AdviceListener adviceListener,List<ConditionBuilder.BuildingForClass> buildingForClassList) {

//        for (ConditionBuilder.BuildingForClass buildingForClass : buildingForClassList) {
//            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
//            try {
//                Thread.currentThread().setContextClassLoader(coreModuleModel.getModuleJarClassLoader());
//                coreModuleModel.getModuleJarClassLoader().loadClass(buildingForClass.getPattern());
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }finally {
//                Thread.currentThread().setContextClassLoader(loader);
//            }
//        }

        final Set<Class<?>> allClassLoadedSet = coreModuleLoadedClassManager.getAllClassLoadedSet();
        final FilterModel filterModel = new FilterModel();
        filterModel.setBuildingForClassList(buildingForClassList);
        filterModel.setAllClassLoadedSet(allClassLoadedSet);
        final WonderClassFileTransformer wonderClassFileTransformer = new WonderClassFileTransformer(filterModel, adviceListener, namespace);
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
