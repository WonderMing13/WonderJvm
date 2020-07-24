package org.wonderming;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 主要去打破类加载的双亲委派机制
 * 让自定义的ClassLoader去加载WonderJvm的Jar包
 * @author wangdeming
 **/
public class WonderClassLoader extends URLClassLoader {

    WonderClassLoader(String wonderJvmCoreJarFilePath) throws MalformedURLException {
        //URL是以file:开头的
        super(new URL[]{new URL("file:" + wonderJvmCoreJarFilePath)});
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
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
