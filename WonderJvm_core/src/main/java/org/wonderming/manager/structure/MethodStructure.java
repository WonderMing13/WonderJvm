package org.wonderming.manager.structure;

import org.wonderming.manager.CoreAccess;
import org.wonderming.manager.CoreClassStructure;

/**
 * @author wangdeming
 **/
public class MethodStructure extends MethodMemberStructure{

    public MethodStructure(CoreAccess coreAccess,
                           String name,
                           CoreClassStructure coreClassStructure) {
        super(coreAccess, name, coreClassStructure);
    }
}
