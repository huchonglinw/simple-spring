package com.huchonglin.core;

import com.huchonglin.annotation.Configuration;
import com.huchonglin.util.ClassUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: hcl
 * @date: 2020/7/5 20:55
 */
public class AnnotationContextReader implements Reader {
    /**
     * 读取的是配置类
     * @param clazz 配置类
     * @return 返回配置类上注解的包路径下 所有的类集合
     */
    @Override
    public Set<Class<?>> read(Class<?> clazz) {
        Set<Class<?>> classSet = new HashSet<>();

        Configuration annotation = clazz.getAnnotation(Configuration.class);
        if (annotation != null) {
            String classPath = annotation.value();
            String[] classPaths = annotation.values();
            if (classPath != "") {
                // 加载指定路径
                classSet = ClassUtil.loadClassFromPackage(classPath);

            }
            if (classPaths.length != 0) {
                // todo if (classPaths.length != 0)
            }
        }
        return classSet;

    }

    public String resolverConfigurationClass(Class<?> clazz) {
        return null;
    }

}
