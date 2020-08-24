package org.wonderming.manager.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wonderming.Module;
import org.wonderming.classloader.ModuleJarClassLoader;
import org.wonderming.CoreModuleEventWatcher;
import org.wonderming.manager.CoreModuleJarLoader;
import org.wonderming.manager.CoreModuleLoadedClassManager;
import org.wonderming.manager.CoreModuleManager;
import org.wonderming.model.CoreModuleModel;
import org.wonderming.utils.JavaInstanceUtil;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.*;

import static org.apache.commons.lang3.reflect.FieldUtils.writeField;

/**
 * @author wangdeming
 **/
public class DefaultCoreModuleManager implements CoreModuleManager {

    private final Logger logger = LoggerFactory.getLogger(DefaultCoreModuleManager.class);

    private final Instrumentation inst;

    private final File[] moduleFileArray;

    private final String namespace;

    private final String systemModulePath;

    private final String userModulePath;

    private final CoreModuleLoadedClassManager coreModuleLoadedClassManager;

    public DefaultCoreModuleManager(Instrumentation inst,
                                    String namespace, String systemModulePath,
                                    String userModulePath,
                                    CoreModuleLoadedClassManager coreModuleLoadedClassManager) {
        this.inst = inst;
        this.namespace = namespace;
        this.systemModulePath = systemModulePath;
        this.userModulePath = userModulePath;
        this.moduleFileArray = getAllModuleFileArray();
        this.coreModuleLoadedClassManager = coreModuleLoadedClassManager;
    }

    private final static Map<String, CoreModuleModel> LOAD_CORE_MODULE_CONFIGURE_MAP = new HashMap<>();

    /**
     * 获取所有moduleFileArray
     * 1.system-module下的module文件
     * 2.user-module下的所有user-module文件
     * @return File[] 文件数组
     */
    private File[] getAllModuleFileArray(){
        final LinkedHashSet<File> files = new LinkedHashSet<>();
        //system-module下的module文件
//        final File file = new File(this.systemModulePath);
//        files.add(file);
        //user-module下的所有user-module文件 不递归
        files.addAll(FileUtils.listFiles(new File(this.userModulePath),new String[]{"jar"},false));
        return files.toArray(new File[]{});
    }

    @Override
    public void load(CoreModuleModel coreModuleModel) {
        //如果该模块已经被ClassLoader持有 则放弃该模块的加载
        if(LOAD_CORE_MODULE_CONFIGURE_MAP.containsKey(coreModuleModel.getModuleId())){
            final CoreModuleModel existedCoreModule = LOAD_CORE_MODULE_CONFIGURE_MAP.get(coreModuleModel.getModuleId());
            logger.debug("module already loaded. exist moduleId={}; module={}", existedCoreModule.getModuleId(),existedCoreModule.getModule());
            return;
        }
        final Module module = coreModuleModel.getModule();
        //获取@Resource注解的
        final List<Field> fieldsListWithAnnotation = FieldUtils.getFieldsListWithAnnotation(module.getClass(), Resource.class);
        fieldsListWithAnnotation.forEach(field -> {
            //instanceof是儿子找父亲 isAssignableFrom是父亲找儿子
            if (CoreModuleEventWatcher.class.isAssignableFrom(field.getType())){
                final CoreModuleEventWatcher coreModuleEventWatcher = coreModuleModel.addReleaseAbleResource(
                        new CoreModuleModel.BaseReleaseAbleResource<CoreModuleEventWatcher>(
                                JavaInstanceUtil.INSTANCE.protectProxy(CoreModuleEventWatcher.class,
                                        new DefaultCoreModuleEventWatcher(coreModuleLoadedClassManager,inst, namespace, coreModuleModel))) {
                            @Override
                            public void release() {
                                logger.info("release resource");
                            }
                        });
                if (coreModuleEventWatcher != null){
                    try {
                        writeField(field,module,coreModuleEventWatcher,true);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //注册到模块中
        LOAD_CORE_MODULE_CONFIGURE_MAP.put(coreModuleModel.getModuleId(),coreModuleModel);
    }

    /**
     * 回调函数作真正的加载
     */
    public class InnerModuleJarLoadCallback implements CoreModuleJarLoader.ModuleJarLoadCallback{
        @Override
        public void onLoad(String moduleId, File moduleJarFile, Module module, ModuleJarClassLoader moduleJarClassLoader) {
            logger.info("loading moduleJarFile: {},module: {},moduleJarClassLoader: {}",moduleJarFile,module,moduleJarClassLoader);
            final CoreModuleModel coreModuleModel = new CoreModuleModel(moduleId, moduleJarFile, module, moduleJarClassLoader);
            load(coreModuleModel);
        }
    }

    @Override
    public void reset() {
        logger.info("force unload all modules:{}",LOAD_CORE_MODULE_CONFIGURE_MAP.keySet());
        //卸载已有module
        final ArrayList<CoreModuleModel> coreModuleModels = new ArrayList<>(LOAD_CORE_MODULE_CONFIGURE_MAP.values());
        for (CoreModuleModel coreModuleModel : coreModuleModels) {
            unload(coreModuleModel);
        }
        //加载module
        for (File moduleJar:moduleFileArray) {
            if (moduleJar.exists() && moduleJar.canRead()){
                CoreModuleJarLoader coreModuleJarLoader = new DefaultCoreModuleJarLoader(moduleJar);
                coreModuleJarLoader.load(
                        new InnerModuleJarLoadCallback()
                );
            }
        }
    }

    /**
     * 关闭ClassLoader
     * @param classLoader ClassLoader
     */
    public void closeModuleJarClassLoaderIfNecessity(ClassLoader classLoader){
        if (classLoader instanceof ModuleJarClassLoader){
            final Collection<CoreModuleModel> coreModuleModels = LOAD_CORE_MODULE_CONFIGURE_MAP.values();
            final boolean match = coreModuleModels.parallelStream().anyMatch(coreModuleModel -> classLoader == coreModuleModel.getModuleJarClassLoader());
            if (match){
                try {
                    ((ModuleJarClassLoader) classLoader).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void unload(CoreModuleModel coreModuleModel) {
        LOAD_CORE_MODULE_CONFIGURE_MAP.remove(coreModuleModel.getModuleId());
        //释放资源
        coreModuleModel.doReleaseAllResource();
        //关闭ClassLoader
        closeModuleJarClassLoaderIfNecessity(coreModuleModel.getModuleJarClassLoader());
    }

    @Override
    public CoreModuleModel get(String moduleId) {
        return LOAD_CORE_MODULE_CONFIGURE_MAP.get(moduleId);
    }
}
