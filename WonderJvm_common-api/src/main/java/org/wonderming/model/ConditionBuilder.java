package org.wonderming.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangdeming
 **/
public class ConditionBuilder {

    public interface IBuildingForClass{

        /**
         * 加载来自BootstrapClassLoader加载的类
         * @return IBuildingForClass
         */
        IBuildingForClass includeBootstrap();

        /**
         * {@link #onClass}所指定的类，检索路径是否包含子类（实现类）
         * <ul>
         * <li>如果onClass()了一个接口，则匹配时会搜索这个接口的所有实现类</li>
         * <li>如果onClass()了一个类，则匹配时会搜索这个类的所有子类</li>
         * </ul>
         *
         * @return IBuildingForClass
         */
        IBuildingForClass includeSubClasses();

        /**
         * 构建行为匹配器，匹配符合模版匹配名称的行为
         * @param pattern 方法名称
         * @return IBuildingForMethod
         */
        IBuildingForMethod onMethod(String pattern);
    }

    public interface IBuildingForMethod{
        /**
         * 继续匹配方法
         * @param pattern 方法名称
         * @return IBuildingForMethod
         */
        IBuildingForMethod onNextMethod(String pattern);
    }

    List<IBuildingForClass> buildingForClassList = new ArrayList<>();

    /**
     * 模版匹配类名称(包含包名)
     * <p>
     * 例子：
     * <ul>
     * <li>"com.alibaba.*"</li>
     * <li>"java.util.ArrayList"</li>
     * </ul>
     *
     * @param pattern 类名匹配模版
     * @return IBuildingForClass
     */
    public IBuildingForClass onClass(final String pattern) {
        final IBuildingForClass buildingForClass = new BuildingForClass(pattern);
        buildingForClassList.add(buildingForClass);
        return buildingForClass;
    }

    private static class BuildingForClass implements IBuildingForClass {

        private final String pattern;

        private boolean isIncludeSubClasses = false;

        private boolean isIncludeBootstrap = false;

        private final List<IBuildingForMethod> buildingForMethodList = new ArrayList<>();

        public BuildingForClass(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public IBuildingForClass includeBootstrap() {
            isIncludeBootstrap = true;
            return this;
        }

        @Override
        public IBuildingForClass includeSubClasses() {
            isIncludeSubClasses = true;
            return this;
        }

        @Override
        public IBuildingForMethod onMethod(String pattern) {
            final IBuildingForMethod buildingForMethod = new BuildingForMethod(this, pattern);
            buildingForMethodList.add(buildingForMethod);
            return buildingForMethod;
        }

        @Override
        public String toString() {
            return "BuildingForClass{" +
                    "pattern='" + pattern + '\'' +
                    ", isIncludeSubClasses=" + isIncludeSubClasses +
                    ", isIncludeBootstrap=" + isIncludeBootstrap +
                    ", buildingForMethodList=" + buildingForMethodList +
                    '}';
        }
    }

    private static class BuildingForMethod implements IBuildingForMethod {

        private final BuildingForClass bfClass;

        private final String pattern;

        public BuildingForMethod(BuildingForClass bfClass, String pattern) {
            this.bfClass = bfClass;
            this.pattern = pattern;
        }

        @Override
        public IBuildingForMethod onNextMethod(String pattern) {
             return bfClass.onMethod(pattern);
        }

        @Override
        public String toString() {
            return "BuildingForMethod{" +
                    "bfClass=" + bfClass +
                    ", pattern='" + pattern + '\'' +
                    '}';
        }
    }


}
