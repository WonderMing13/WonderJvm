package org.wonderming.manager.filter;

import org.wonderming.manager.Filter;
import org.wonderming.manager.structure.MethodStructure;
import org.wonderming.model.ConditionBuilder;
import org.wonderming.model.FilterModel;
import org.wonderming.model.MatchingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangdeming
 **/
public class DefaultMethodFilter implements Filter {

    @Override
    public boolean operator(FilterModel filterModel, FilterChain filterChain) {
        final MatchingResult matchingResult = filterChain.getMatchingResult();
        final List<MethodStructure> methodStructureList = new ArrayList<>();
        for (MethodStructure methodStructure : filterModel.getCoreClassStructure().getMethodStructure()) {
            for (ConditionBuilder.BuildingForClass buildingForClass : matchingResult.getFilterClassList()) {
                buildingForClass.getBuildingForMethodList().forEach(buildingForMethod -> {
                    if (methodStructure.getName().equals(buildingForMethod.getPattern())){
                        methodStructureList.add(methodStructure);
                    }
                });
            }
        }
        if (!methodStructureList.isEmpty()){
            matchingResult.setMethodStructureList(methodStructureList);
            return true;
        }
        return false;
    }
}
