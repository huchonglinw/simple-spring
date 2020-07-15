package com.huchonglin.core;

/**
 * Bean工厂接口，单一职责，只负责getBean
 * @author: hcl
 * @date: 2020/7/4 21:08
 */
public interface BeanFactory {
    /**
     * 获取bean
     * @param name beanName
     * @return
     */
    Object getBean(String name);
}
