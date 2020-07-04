package com.huchonglin;

import lombok.Getter;
import lombok.Setter;

/**
 * 对Class的一层封装
 * @author: hcl
 * @date: 2020/7/2 20:12
 */
@Getter
@Setter
public class BeanDefinition {
    /**
     * 所属的类
     */
    private Class<?> clazz;
    /**
     * Bean的名字
     */
    private String beanName;

    /**
     * 保存对象的引用 用于代理
     */
    private Object object;

    public BeanDefinition(Class<?> clazz, String beanName) {
        this.clazz = clazz;
        this.beanName = beanName;
    }
}
