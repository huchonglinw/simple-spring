package com.huchonglin.core;

import java.lang.reflect.Method;

/**
 * 代理的元数据信息
 * @author: hcl
 * @date: 2020/7/9 22:04
 */
public class ProxyMetaData {
    private Method before;
    private Method after;
    private Object methodObject;
    public ProxyMetaData(Method before, Method after, Object methodObject) {
        this.before = before;
        this.after = after;
        this.methodObject = methodObject;
    }

    public Method getBefore() {
        return before;
    }

    public void setBefore(Method before) {
        this.before = before;
    }

    public Method getAfter() {
        return after;
    }

    public void setAfter(Method after) {
        this.after = after;
    }

    public Object getMethodObject() {
        return methodObject;
    }

    public void setMethodObject(Object methodObject) {
        this.methodObject = methodObject;
    }
}
