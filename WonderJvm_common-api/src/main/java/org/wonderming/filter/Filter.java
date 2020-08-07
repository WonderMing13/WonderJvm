package org.wonderming.filter;

import org.wonderming.model.ConditionBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangdeming
 **/
public interface Filter{

    public void operator(ConditionBuilder conditionBuilder,FilterChain filterChain);

}
