package org.wonderming.model;

import org.wonderming.Module;
import org.wonderming.asm.WonderClassFileTransformer;
import org.wonderming.classloader.ModuleJarClassLoader;

import java.io.File;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wangdeming
 **/
public class CoreModuleModel {
    /**
     * 每个module持有的唯一id
     */
    private final String moduleId;

    /**
     * 需要形变的类转换器
     */
    private final Set<WonderClassFileTransformer> wonderClassFileTransformers = new LinkedHashSet<>();

    /**
     * 代表的module jar包
     * 1.system-module的jar包
     * 2.user-module的jar包
     */
    private final File moduleJarFile;

    /**
     * 所属模块
     */
    private final Module module;

    /**
     * module加载的ClassLoader
     */
    private final ModuleJarClassLoader moduleJarClassLoader;

    /**
     * 弱引用的可释放资源
     */
    private final List<BaseReleaseAbleResource<?>> baseReleaseAbleResources = new ArrayList<>();

    /**
     * 匿名类
     * 1.强引用:引用要是没消失则不会被垃圾回收
     * 2.软引用:内存空间足够则不会回收;内存空间不足则会回收
     * 3.弱引用:一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存
     * 4.虚引用:虚引用必须和引用队列 （ReferenceQueue）联合使用。当垃圾回收器准备回收一个对象时，如果发现它还有虚引用，就会在回收对象的内存之前，把这个虚引用加入到与之 关联的引用队列中
     * @param <T>
     */
    public static abstract class BaseReleaseAbleResource<T> {

        private final ReferenceQueue<T> referenceQueue = new ReferenceQueue<>();

        private final WeakReference<T> reference;

        protected BaseReleaseAbleResource(T resource) {
            this.reference = new WeakReference<T>(resource,referenceQueue);
        }

        /**
         * 释放资源
         */
        public abstract void release();

        public T getResource(){
            //软引用已经回收了
            return referenceQueue.poll() == reference ? null : reference.get();
        }
    }

    /**
     * 添加可释放资源List
     * @param releaseAbleResource BaseReleaseAbleResource
     * @param <T> 泛型
     * @return T
     */
    public synchronized  <T> T addReleaseAbleResource(BaseReleaseAbleResource<T> releaseAbleResource){
        if (releaseAbleResource.getResource() == null){
            return null;
        }else {
            baseReleaseAbleResources.add(releaseAbleResource);
            return releaseAbleResource.getResource();
        }
    }

    /**
     * 执行释放资源
     */
    public void doReleaseAllResource(){
        //释放资源
        baseReleaseAbleResources.forEach(BaseReleaseAbleResource::release);
        baseReleaseAbleResources.clear();
    }


    public CoreModuleModel(String moduleId, File moduleJarFile, Module module,ModuleJarClassLoader moduleJarClassLoader) {
        this.moduleId = moduleId;
        this.moduleJarFile = moduleJarFile;
        this.module = module;
        this.moduleJarClassLoader = moduleJarClassLoader;
    }

    public String getModuleId() {
        return moduleId;
    }

    public Set<WonderClassFileTransformer> getWonderClassFileTransformers() {
        return wonderClassFileTransformers;
    }

    public File getModuleJarFile() {
        return moduleJarFile;
    }

    public ModuleJarClassLoader getModuleJarClassLoader() {
        return moduleJarClassLoader;
    }

    public Module getModule(){
        return module;
    }
}
