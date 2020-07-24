package org.wonderming.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author wangdeming
 **/
public class ModuleJarClassLoader extends URLClassLoader {

    public ModuleJarClassLoader(URL[] urls) {
        super(urls);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}
