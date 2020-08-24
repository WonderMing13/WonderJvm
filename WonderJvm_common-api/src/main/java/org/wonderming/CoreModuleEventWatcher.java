package org.wonderming;

import org.wonderming.model.AdviceListener;
import org.wonderming.model.ConditionBuilder;

import java.util.List;

/**
 * @author wangdeming
 **/
public interface CoreModuleEventWatcher {

    public void watch(AdviceListener adviceListener,List<ConditionBuilder.BuildingForClass> buildingForClassList);
}
