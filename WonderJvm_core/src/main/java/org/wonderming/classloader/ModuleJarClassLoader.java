package org.wonderming.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wonderming.manager.impl.DefaultCoreModuleJarLoader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

/**
 * @author wangdeming
 **/
public class ModuleJarClassLoader extends URLClassLoader {

    @Override
    public URL getResource(String name) {
        URL url = findResource(name);
        if (null != url) {
            return url;
        }
        url = super.getResource(name);
        return url;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> urls = findResources(name);
        if (null != urls) {
            return urls;
        }
        urls = super.getResources(name);
        return urls;
    }

    public ModuleJarClassLoader(File moduleJarFile) throws MalformedURLException {
        super(new URL[]{new URL("file:" + moduleJarFile.getPath())});
        Logger logger = LoggerFactory.getLogger(ModuleJarClassLoader.class);
        logger.info("load moduleJarFile path is :{}",moduleJarFile.getPath());
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            //直接用该ClassLoader去加载
            return ModuleJarClassLoader.class.getClassLoader().loadClass(name);
        }catch (Exception e){
            //ignore
        }
        //试着去寻找该类是否被加载过
        final Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null){
            return loadedClass;
        }
        try {
            //直接从该ClassLoader去加载 不需要去直接使用父类加载器 不是用一个类加载器加载时 equals会报错 :)
            final Class<?> aClass = findClass(name);
            if (resolve){
                resolveClass(aClass);
            }
            return aClass;
        }catch (Exception e){
            //如果走到这一步 说明即使破坏双亲委派机制依旧没法加载到该类 又得乖乖让父类加载器去加载
            return super.loadClass(name,resolve);
        }
    }
}
