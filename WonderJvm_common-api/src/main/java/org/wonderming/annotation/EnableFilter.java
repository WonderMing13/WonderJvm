package org.wonderming.annotation;

import java.lang.annotation.*;

/**
 * @author wangdeming
 **/
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableFilter {

}
