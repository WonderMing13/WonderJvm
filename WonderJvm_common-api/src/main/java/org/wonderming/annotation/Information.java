package org.wonderming.annotation;

import java.lang.annotation.*;

/**
 * @author wangdeming
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Information {

    /**
     * module唯一性的Id
     * @return String
     */
    String moduleId();

    /**
     * 模块作者的名字
     * @return String
     */
    String authorName();
}
