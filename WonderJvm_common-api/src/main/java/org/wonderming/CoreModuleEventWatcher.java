package org.wonderming;

import org.wonderming.model.ConditionBuilder;

import java.util.List;

/**
 * @author wangdeming
 **/
public interface CoreModuleEventWatcher {

    public void watch(List<ConditionBuilder.BuildingForClass> buildingForClassList);
}
