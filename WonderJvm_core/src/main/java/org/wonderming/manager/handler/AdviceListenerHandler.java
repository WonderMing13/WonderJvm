package org.wonderming.manager.handler;

import org.wonderming.Spy;
import org.wonderming.SpyHandler;
import org.wonderming.model.AdviceListener;
import org.wonderming.utils.ObjectIdUtil;

/**
 * @author wangdeming
 **/
public class AdviceListenerHandler implements SpyHandler {

    private final static AdviceListenerHandler SINGLE_TON = new AdviceListenerHandler();

    public static AdviceListenerHandler getSingleton() {
        return SINGLE_TON;
    }


    @Override
    public Spy.SpyOfResult handleSpyMethodCallBefore(Object[] argumentArray, int targetClassLoaderId, int listenerId, String targetClassName, String targetMethodName, String targetDes, Object target) {
        AdviceListener adviceListener = ObjectIdUtil.OBJECT_ID_UTIL.get(listenerId);
        adviceListener.before();
        return new Spy.SpyOfResult(Spy.SpyOfResult.SPY_RESULT_STATE_RETURN,"spy is ok");
    }
}
