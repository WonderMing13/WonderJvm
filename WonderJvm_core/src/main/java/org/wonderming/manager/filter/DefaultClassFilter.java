package org.wonderming.manager.filter;

import org.wonderming.manager.Filter;
import org.wonderming.model.ConditionBuilder;
import org.wonderming.model.FilterModel;
import org.wonderming.model.MatchingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangdeming
 **/
public class DefaultClassFilter implements Filter {

    @Override
    public boolean operator(FilterModel filterModel, FilterChain filterChain) {
        final List<ConditionBuilder.BuildingForClass> filterClassList = new ArrayList<>();
        for (ConditionBuilder.BuildingForClass buildingForClass : filterModel.getBuildingForClassList()) {
            for (Class<?> classFilter : filterModel.getAllClassLoadedSet()) {
                if (buildingForClass.getPattern().equals(classFilter.getName())){
                    filterClassList.add(buildingForClass);
                }
            }
        }
        if (!filterClassList.isEmpty()){
            final MatchingResult matchingResult = new MatchingResult();
            matchingResult.setFilterClassList(filterClassList);
            filterChain.setMatchingResult(matchingResult);
            return true;
        }
        return false;
    }
}
