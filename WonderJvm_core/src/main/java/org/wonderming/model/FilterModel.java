package org.wonderming.model;

import org.wonderming.manager.CoreClassStructure;

import java.util.List;
import java.util.Set;

/**
 * @author wangdeming
 **/
public class FilterModel {

    private Set<Class<?>> allClassLoadedSet;

    private List<ConditionBuilder.BuildingForClass> buildingForClassList;

    private CoreClassStructure coreClassStructure;

    public CoreClassStructure getCoreClassStructure() {
        return coreClassStructure;
    }

    public void setCoreClassStructure(CoreClassStructure coreClassStructure) {
        this.coreClassStructure = coreClassStructure;
    }

    public Set<Class<?>> getAllClassLoadedSet() {
        return allClassLoadedSet;
    }

    public void setAllClassLoadedSet(Set<Class<?>> allClassLoadedSet) {
        this.allClassLoadedSet = allClassLoadedSet;
    }

    public List<ConditionBuilder.BuildingForClass> getBuildingForClassList() {
        return buildingForClassList;
    }

    public void setBuildingForClassList(List<ConditionBuilder.BuildingForClass> buildingForClassList) {
        this.buildingForClassList = buildingForClassList;
    }
}
