package org.wonderming.filter;

import org.wonderming.model.ConditionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

/**
 * @author wangdeming
 **/
public class FilterChain {

    private int index;

    private final List<Filter> filterList = new ArrayList<>();

    public FilterChain addFilter(Filter filter){
        this.filterList.add(filter);
        return this;
    }

    public void build(ConditionBuilder conditionBuilder,FilterChain filterChain){
        if (index == filterList.size()){
            return;
        }
        final Filter filter = filterList.get(index);
        index++;
        filter.operator(conditionBuilder,filterChain);
    }



}
