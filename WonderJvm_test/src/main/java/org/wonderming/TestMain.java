package org.wonderming;

import org.wonderming.annotation.Command;
import org.wonderming.annotation.Information;
import org.wonderming.model.AdviceListener;
import org.wonderming.model.ConditionBuilder;

import javax.annotation.Resource;

/**
 * @author wangdeming
 **/
@Information(moduleId = "hjp", authorName = "WonderMing")
public class TestMain implements Module {

    @Resource
    private CoreModuleEventWatcher coreModuleEventWatcher;

    @Command(methodValue = "/xjx")
    public void test() {
        new ConditionBuilder(coreModuleEventWatcher)
                .onClass("org.wonderming.Test")
                .onMethod("checkState")
                .onWatch()
                .watching(new AdviceListener() {
                    @Override
                    public void before() {
                        System.out.println("fuck hjp");
                    }
                });
    }
}
