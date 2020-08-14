package org.wonderming.manager;

import org.wonderming.manager.filter.FilterChain;
import org.wonderming.model.FilterModel;

/**
 * @author wangdeming
 **/
public interface Filter{

    public boolean operator(FilterModel filterModel, FilterChain filterChain);

}
