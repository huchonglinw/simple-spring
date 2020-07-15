package com.huchonglin.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * ServletContext、HttpServletRequest、HttpServletResponse的封装
 * 由于每个线程的ServletContextBean都不一样
 * 我们可以选择 对每个req都封装一次，但是需要保存起来，用到的时候去拿；
 * 或者我们可以直接用ThreadLocal
 * @author: hcl
 * @date: 2020/7/5 10:49
 */
public class ServletContextBean {
    private ServletContext servletContext;
    private HttpServletRequest request;
    private HttpSession httpSession;
    private HttpServletResponse response;

    public ServletContextBean(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.httpSession=request.getSession();
        this.servletContext=request.getServletContext();
    }

    public javax.servlet.ServletContext getServletContext() {
        return servletContext;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
