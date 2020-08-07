package org.wonderming.manager.structure;

import org.wonderming.manager.CoreAccess;
import org.wonderming.manager.CoreClassStructure;

/**
 * @author wangdeming
 **/
public class MethodMemberStructure {

    /**
     * 访问权限
     */
    private final CoreAccess coreAccess;

    /**
     * 方法名称
     */
    private final String name;

    /**
     * 所属类的信息
     */
    private final CoreClassStructure coreClassStructure;

    public MethodMemberStructure(CoreAccess coreAccess, String name, CoreClassStructure coreClassStructure) {
        this.coreAccess = coreAccess;
        this.name = name;
        this.coreClassStructure = coreClassStructure;
    }

    public CoreAccess getCoreAccess() {
        return coreAccess;
    }

    public String getName() {
        return name;
    }

    public CoreClassStructure getCoreClassStructure() {
        return coreClassStructure;
    }
}
