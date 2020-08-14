package org.wonderming.model;

import org.wonderming.manager.CoreModuleManager;
import org.wonderming.manager.impl.DefaultCoreLoadedClassSource;
import org.wonderming.manager.impl.DefaultCoreModuleManager;
import org.wonderming.utils.JavaInstanceUtil;

import java.lang.instrument.Instrumentation;

/**
 * @author wangdeming
 **/
public class JvmModel{
    private final String systemModulePath;

    private final String userModulePath;

    private final Instrumentation inst;

    private final CoreModuleManager coreModuleManager;

    public JvmModel(String systemModulePath, String userModulePath, Instrumentation inst) {
        this.systemModulePath = systemModulePath;
        this.userModulePath = userModulePath;
        this.inst = inst;
        //执行默认的构造函数
        this.coreModuleManager = JavaInstanceUtil.INSTANCE.protectProxy(
                CoreModuleManager.class, new DefaultCoreModuleManager(
                inst,
                systemModulePath,
                userModulePath,
                new DefaultCoreLoadedClassSource(inst)));
    }

    public CoreModuleManager getCoreModuleManager() {
        return coreModuleManager;
    }

    public String getSystemModulePath() {
        return systemModulePath;
    }

    public String getUserModulePath() {
        return userModulePath;
    }

    public Instrumentation getInst() {
        return inst;
    }
}
