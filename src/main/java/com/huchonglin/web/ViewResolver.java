package com.huchonglin.web;

/**
 * 视图解析器接口
 * @author: hcl
 * @date: 2020/7/5 14:20
 */
public interface ViewResolver<T> {
    /**
     * 解析
     * @param resolveObject 解析的对象
     * @return
     */
    T resolver(T resolveObject);
}
