package com.huchonglin.core;

/**
 * 标志性接口了，在一个Bean走完Aware接口之后会调用到这里
 * 相当于是一个 BeanPostProcessorListener
 * 逻辑：
 * @author: hcl
 * @date: 2020/7/16 17:33
 */
public interface BeanPostProcessor {
    /**
     * Bean实例化之后调用的
     * @param bean
     * @return
     */
    Object after(Object bean);

    /**
     * Bean实例化之前调用的
     * @param bean
     * @return
     */
    Object before(Object bean);
}
