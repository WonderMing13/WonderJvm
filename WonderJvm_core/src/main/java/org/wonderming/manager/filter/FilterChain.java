package org.wonderming.manager.filter;

import org.wonderming.manager.Filter;
import org.wonderming.model.FilterModel;
import org.wonderming.model.MatchingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangdeming
 **/
public class FilterChain {

    private int index;

    private final List<Filter> filterList = new ArrayList<>();

    private MatchingResult matchingResult;

    public MatchingResult getMatchingResult() {
        return matchingResult;
    }

    public void setMatchingResult(MatchingResult matchingResult) {
        this.matchingResult = matchingResult;
    }

    public FilterChain addFilter(Filter filter){
        this.filterList.add(filter);
        return this;
    }

    public MatchingResult build(FilterModel filterModel, FilterChain filterChain){
        do {
            final Filter filter = filterList.get(index);
            if (filter.operator(filterModel,filterChain)){
                index++;
            }else {
                break;
            }
        }while (index != filterList.size());
        return filterChain.getMatchingResult();
    }

}
