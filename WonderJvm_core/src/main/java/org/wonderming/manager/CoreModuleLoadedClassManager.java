package org.wonderming.manager;

import org.wonderming.model.ConditionBuilder;
import org.wonderming.model.FilterModel;

import java.util.List;
import java.util.Set;

/**
 * @author wangdeming
 **/
public interface CoreModuleLoadedClassManager {

    /**
     * Returns an array of all classes currently loaded by the JVM.
     * 获取被JVM加载的类
     * @return Set<Class<?>>
     */
    Set<Class<?>> getAllClassLoadedSet();

    /**
     * 找到需要形变的类
     * @return List<Class<?>> 需要形变的类
     */
    List<Class<?>> findForReTransform(List<ConditionBuilder.BuildingForClass> buildingForClassList);
}
