package com.huchonglin.util;

import com.huchonglin.web.ServletContextBean;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author: hcl
 * @date: 2020/7/5 10:59
 */
public class ServletContextUtil {
    private static final ThreadLocal<ServletContextBean> THREAD_LOCAL = new ThreadLocal<>();
    public static ThreadLocal<ServletContextBean> getThreadLocal(){
        return THREAD_LOCAL;
    }
    public static ServletContext getServetContext() {
        ServletContextBean servletContextBean = THREAD_LOCAL.get();
        return servletContextBean.getServletContext();
    }

    public static HttpServletRequest getHttpRequest() {
        ServletContextBean servletContextBean = THREAD_LOCAL.get();
        return servletContextBean.getRequest();
    }

    public static HttpServletResponse getHttpResponse() {
        ServletContextBean servletContextBean = THREAD_LOCAL.get();
        return servletContextBean.getResponse();
    }

    public static HttpSession getHttpSession() {
        ServletContextBean servletContextBean = THREAD_LOCAL.get();
        return servletContextBean.getHttpSession();
    }

}
