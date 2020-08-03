package org.wonderming.manager.impl;

import org.wonderming.manager.EventListener;
import org.wonderming.model.EventModel;

/**
 * 适配监听器
 * @author wangdeming
 **/
public class AdviceAdapterListener implements EventListener {

    private AdviceListener adviceListener;

    public AdviceAdapterListener(AdviceListener adviceListener) {
        this.adviceListener = adviceListener;
    }

    @Override
    public void listen(EventModel eventModel) {

    }
}
