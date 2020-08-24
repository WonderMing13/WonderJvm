package org.wonderming.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 模仿Sandbox
 * @author wangdeming
 **/
public class ObjectIdUtil {

    public final static ObjectIdUtil OBJECT_ID_UTIL = new ObjectIdUtil();

    private final static int NULL_ID = 0;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final SequenceUtil sequenceUtil = new SequenceUtil();

    /**
     * {@link WeakHashMap} 弱引用的HashMap
     */
    private final WeakHashMap<Object,Integer> objectIdWeakHashMap = new WeakHashMap<>();

    private final ReferenceQueue<Object> rQueue = new ReferenceQueue<>();

    private final HashMap<Integer,IdWeakReference> idWeakReferenceHashMap = new HashMap<>();

    /**
     *
     * @param object 待映射的Java对象
     * @return 全局对象ID
     */
    public int put(final Object object){
        if (null == object){
            return NULL_ID;
        }
        //读锁不阻塞
        rwLock.readLock().lock();
        try {
            final Integer objectId = objectIdWeakHashMap.get(object);
            if (null != objectId){
                return objectId;
            }
        }finally {
            rwLock.readLock().unlock();
            cleanWeakReferenceMap();
        }
        //说明object从未映射过
        rwLock.writeLock().lock();
        try {
            int nextObjectId;
            objectIdWeakHashMap.put(object,nextObjectId = sequenceUtil.next());
            idWeakReferenceHashMap.put(nextObjectId,new IdWeakReference(nextObjectId,object));
            return nextObjectId;
        }finally {
            rwLock.writeLock().unlock();
        }
    }

    public <T> T get(int objectId){
        if (NULL_ID == objectId){
            return null;
        }
        rwLock.readLock().lock();
        try {
            final Object object;
            final IdWeakReference idWeakReference = idWeakReferenceHashMap.get(objectId);
            if (null != idWeakReference && null != (object = idWeakReference.get())){
                return (T)object;
            }else {
                return null;
            }
        }finally {
            rwLock.readLock().unlock();
            cleanWeakReferenceMap();
        }
    }

    private void cleanWeakReferenceMap(){
        for (Object obj;(obj = rQueue.poll()) != null;){
            rwLock.writeLock().lock();
            try {
                idWeakReferenceHashMap.remove(((IdWeakReference)obj).objectId);
            }finally {
                rwLock.writeLock().unlock();
            }
        }
    }

    private class IdWeakReference extends WeakReference<Object> {

        private final Integer objectId;

        public IdWeakReference(Integer objectId,Object referent) {
            super(referent,rQueue);
            this.objectId = objectId;
        }
    }

}
