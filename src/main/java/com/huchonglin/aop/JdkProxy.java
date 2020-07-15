package com.huchonglin.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author: hcl
 * @date: 2020/7/3 00:11
 */
public class JdkProxy implements AopProxy {
    private Object object;
    private Method beforeMethod;
    private Method afterMethod;
    private Object aspectObject;

    public JdkProxy(Object object, Method beforeMethod, Method afterMethod, Object aspectObject) {
        this.object = object;
        this.beforeMethod = beforeMethod;
        this.afterMethod = afterMethod;
        this.aspectObject = aspectObject;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(),
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (beforeMethod != null) {
                        beforeMethod.invoke(aspectObject);
                    }
                    method.invoke(object);
                    if (afterMethod != null) {
                        afterMethod.invoke(aspectObject);
                    }
                    return proxy;
                }
            });
    }
}
