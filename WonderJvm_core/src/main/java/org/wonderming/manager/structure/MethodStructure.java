package org.wonderming.manager.structure;

import org.wonderming.manager.CoreAccess;
import org.wonderming.manager.CoreClassStructure;

import java.util.List;

/**
 * @author wangdeming
 **/
public class MethodStructure extends MethodMemberStructure{
    /**
     * 返回类型的类结构
     */
    private final CoreClassStructure returnTypeClassStructure;
    /**
     * 参数的类结构
     */
    private final List<CoreClassStructure> parameterTypeClassStructures;
    /**
     * 异常的类结构
     */
    private final List<CoreClassStructure> exceptionTypeClassStructures;
    /**
     * 注解的类结构
     */
    private final List<CoreClassStructure> annotationTypeClassStructures;

    public MethodStructure(CoreAccess coreAccess,
                           String name,
                           CoreClassStructure declaringClassStructure,
                           CoreClassStructure returnTypeClassStructure,
                           List<CoreClassStructure> parameterTypeClassStructures,
                           List<CoreClassStructure> exceptionTypeClassStructures,
                           List<CoreClassStructure> annotationTypeClassStructures) {
        super(coreAccess, name, declaringClassStructure);
        this.returnTypeClassStructure = returnTypeClassStructure;
        this.parameterTypeClassStructures = parameterTypeClassStructures;
        this.exceptionTypeClassStructures = exceptionTypeClassStructures;
        this.annotationTypeClassStructures = annotationTypeClassStructures;
    }

    public CoreClassStructure getReturnTypeClassStructure() {
        return returnTypeClassStructure;
    }

    public List<CoreClassStructure> getParameterTypeClassStructures() {
        return parameterTypeClassStructures;
    }

    public List<CoreClassStructure> getExceptionTypeClassStructures() {
        return exceptionTypeClassStructures;
    }

    public List<CoreClassStructure> getAnnotationTypeClassStructures() {
        return annotationTypeClassStructures;
    }

    @Override
    public String toString() {
        return "MethodStructure{" +
                "returnTypeClassStructure=" + returnTypeClassStructure +
                ", parameterTypeClassStructures=" + parameterTypeClassStructures +
                ", exceptionTypeClassStructures=" + exceptionTypeClassStructures +
                ", annotationTypeClassStructures=" + annotationTypeClassStructures +
                '}';
    }
}
