package org.wonderming.manager;

import org.wonderming.Module;
import org.wonderming.classloader.ModuleJarClassLoader;

import java.io.File;

/**
 * @author wangdeming
 **/
public interface CoreModuleJarLoader {

    public interface ModuleJarLoadCallback{
        void onLoad(String moduleId,File moduleJarFile, Module module, ModuleJarClassLoader moduleJarClassLoader);
    }

    public void load(ModuleJarLoadCallback mjL);

}
