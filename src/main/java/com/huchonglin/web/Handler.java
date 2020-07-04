package com.huchonglin.web;

import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Controller类、Controller.object、Method、Url的封装
 * @author: hcl
 * @date: 2020/7/3 23:45
 */
@Getter
@Setter
@AllArgsConstructor
public class Handler {
    private Class<?> controllerClass;
    private Object controllerObject;
    private Method method;
    private String url;

}
