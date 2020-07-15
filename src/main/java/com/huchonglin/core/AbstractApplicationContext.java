package com.huchonglin.core;

/**
 * @author: hcl
 * @date: 2020/7/5 20:53
 */
public class AbstractApplicationContext implements ApplicationContext {
    private BeanFactory beanFactory;

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public Object getBean(String name){
        return beanFactory.getBean(name);
    }

}
