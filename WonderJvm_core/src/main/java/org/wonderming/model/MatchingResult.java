package org.wonderming.model;

import org.wonderming.manager.structure.MethodStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangdeming
 **/
public class MatchingResult {

    private List<ConditionBuilder.BuildingForClass> filterClassList = new ArrayList<>();

    private List<MethodStructure> methodStructureList = new ArrayList<>();

    public List<MethodStructure> getMethodStructureList() {
        return methodStructureList;
    }

    public void setMethodStructureList(List<MethodStructure> methodStructureList) {
        this.methodStructureList = methodStructureList;
    }

    public List<ConditionBuilder.BuildingForClass> getFilterClassList() {
        return filterClassList;
    }

    public void setFilterClassList(List<ConditionBuilder.BuildingForClass> filterClassList) {
        this.filterClassList = filterClassList;
    }
}
