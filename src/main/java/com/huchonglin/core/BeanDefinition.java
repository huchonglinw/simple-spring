package com.huchonglin.core;


/**
 * 对Class的一层封装
 * @author: hcl
 * @date: 2020/7/2 20:12
 */
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

    /**
     * 是否需要被代理
     */
    private Boolean toBeProxy;

    /**
     * 该Bean注解的类型
     */
    private int annotationType;

    public BeanDefinition(Class<?> clazz, String beanName) {
        this.clazz = clazz;
        this.beanName = beanName;
        this.toBeProxy = false;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Boolean getToBeProxy() {
        return toBeProxy;
    }

    public void setToBeProxy(Boolean toBeProxy) {
        this.toBeProxy = toBeProxy;
    }

    public int getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(int annotationType) {
        this.annotationType = annotationType;
    }
}
