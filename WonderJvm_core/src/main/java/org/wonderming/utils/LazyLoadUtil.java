package org.wonderming.utils;

/**
 * 懒加载
 * @author wangdeming
 **/
public class LazyLoadUtil {

    public abstract class BaseLazyLoad<T> {

        private volatile boolean initFlag = false;

        private volatile T object;

        public abstract T initValue();

        public T get(){
            if (initFlag){
                return object;
            }
            object = initValue();
            initFlag = true;
            return object;
        }

    }
}
