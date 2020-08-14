package org.wonderming.manager;

import org.wonderming.model.CoreModuleModel;

/**
 * @author wangdeming
 **/
public interface CoreModuleManager {

    /**
     * 加载制定模块
     * @param coreModuleModel 模块的实体类
     */
    public void load(CoreModuleModel coreModuleModel);

    /**
     * 重置环境
     */
    public void reset();

    /**
     * 卸载制定模块
     * @param coreModuleModel 模块的实体类
     */
    public void unload(CoreModuleModel coreModuleModel);

    /**
     * 根据moduleId获取模块
     * @param moduleId String 模块Id
     * @return CoreModuleModel 模块的实体类
     */
    public CoreModuleModel get(String moduleId);



}
