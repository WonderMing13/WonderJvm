package org.wonderming;

import org.wonderming.annotation.Command;
import org.wonderming.annotation.Information;

/**
 * @author wangdeming
 **/
@Information(moduleId = "hjp",authorName = "WonderMing")
public class TestMain implements Module{

    @Command(methodValue = "/xjx")
    public void test(){
        System.out.println("I am wonder");
    }
}
