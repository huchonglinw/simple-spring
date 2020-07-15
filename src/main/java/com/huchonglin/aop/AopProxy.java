package com.huchonglin.aop;

/**
 * @author: hcl
 * @date: 2020/7/4 18:26
 */
public interface AopProxy {
    /**
     * 获取一个代理对象
     * @return 代理对象
     */
    Object getProxy();
}
