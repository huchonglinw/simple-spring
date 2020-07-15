package com.huchonglin.web;

import java.lang.reflect.Method;


/**
 * 描述一个Handler信息 Handler的封装类
 * 封装了Controller类、Controller.object、Method、Url，参数
 * @author: hcl
 * @date: 2020/7/3 23:45
 */
public class Handler {
    private Class<?> controllerClass;
    private Object controllerObject;
    private Method method;
    private String url;
    private Object[] methodArguments;
    public Handler(Class<?> controllerClass, Object controllerObject, Method method, String url) {
        this.controllerClass = controllerClass;
        this.controllerObject = controllerObject;
        this.method = method;
        this.url = url;
    }

    public Object[] getMethodArguments() {
        return methodArguments;
    }

    public void setMethodArgments(Object[] methodArgments) {
        this.methodArguments = methodArgments;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Object getControllerObject() {
        return controllerObject;
    }

    public Method getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

}
