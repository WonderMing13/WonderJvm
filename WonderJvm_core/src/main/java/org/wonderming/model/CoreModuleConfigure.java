package org.wonderming.model;

import org.wonderming.asm.WonderClassFileTransformer;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author wangdeming
 **/
public class CoreModuleConfigure {
    /**
     * 每个module持有的唯一id
     */
    private long moduleId;

    private Set<WonderClassFileTransformer> wonderClassFileTransformers = new LinkedHashSet<>();


}
