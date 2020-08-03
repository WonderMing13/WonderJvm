package org.wonderming.manager.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wonderming.Module;
import org.wonderming.annotation.Information;
import org.wonderming.classloader.ModuleJarClassLoader;
import org.wonderming.manager.CoreModuleJarLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ServiceLoader;

/**
 * @author wangdeming
 **/
public class DefaultCoreModuleJarLoader implements CoreModuleJarLoader {

    private final Logger logger = LoggerFactory.getLogger(DefaultCoreModuleJarLoader.class);

    private final File moduleJarFile;

    public DefaultCoreModuleJarLoader(File moduleJarFile) {
        this.moduleJarFile = moduleJarFile;
    }

    @Override
    public void load(ModuleJarLoadCallback mjL) {
        ModuleJarClassLoader moduleJarClassLoader = null;
        try {
            logger.info("prepare loading moduleJarFile: {}",moduleJarFile);
            moduleJarClassLoader = new ModuleJarClassLoader(moduleJarFile);
            //需要用到ModuleJarClassLoader来加载类 则需要更换线程的ContextClassLoader 此为之前CLassLoader
            final ClassLoader preContextClassLoader = Thread.currentThread().getContextClassLoader();
            //设置ContextClassLoader 使用ModuleJarClassLoader来动态的加载类
            Thread.currentThread().setContextClassLoader(moduleJarClassLoader);
            try{
                //Spi机制
                ServiceLoader<Module> moduleServiceLoader = ServiceLoader.load(Module.class,moduleJarClassLoader);
                for (Module module:moduleServiceLoader) {
                    //获取模块Id
                    final Class<? extends Module> moduleClass = module.getClass();
                    final Information information = moduleClass.getAnnotation(Information.class);
                    final String moduleId = information.moduleId();
                    //检验moduleId
                    if (StringUtils.isBlank(moduleId)){
                        logger.warn("loading module instance error:@Information id is missing,class={};module-jar={}",
                                moduleClass,
                                moduleJarFile);
                        continue;
                    }
                    //请求回调函数
                    mjL.onLoad(moduleId,moduleJarFile,module,moduleJarClassLoader);
                }
            }finally {
                Thread.currentThread().setContextClassLoader(preContextClassLoader);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
