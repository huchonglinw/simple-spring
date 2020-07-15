package com.huchonglin.core;

import java.util.Set;

/**
 * 注解的应用上下文
 *
 * @author: hcl
 * @date: 2020/7/2 19:45
 */
public class AnnotationConfigApplicationContext extends AbstractApplicationContext {
    private AnnotationContextReader reader = new AnnotationContextReader();

    private DefaultBeanFactory beanFactory = new DefaultBeanFactory();

    /**
     * 配置类的构造函数
     */
    public AnnotationConfigApplicationContext(Class<?> configurationClass) {
        register(configurationClass);
        refresh();
    }

    /**
     * 职责：注册Set集合 逻辑：读取类信息，加载指定路径下的所有类，并将类分类加进Set集合中
     *
     * @param clazz
     */
    private void register(Class<?> clazz) {
        //所有的类集合
        Set<Class<?>> classSet = reader.read(clazz);
        if (classSet == null || classSet.isEmpty()) {
            return;
        }
        // 解耦 交给beanFactory去注册bd
        beanFactory.generateBeanDefinition(classSet);
        beanFactory.resolveAspectClass();
    }

    /**
     * 刷新ioc容器 这里的refresh是一个模板方法，后期改进
     */
    public void refresh() {

        // proxyInitialization(beanFactory);

        beanFactory.refresh();

        // finishBeanFactoryInitialization(beanFactory);
    }

    /**
     * @param name
     * @return
     */
    private Object doGetBean(String name) {
        return beanFactory.getBean(name);
    }

}
