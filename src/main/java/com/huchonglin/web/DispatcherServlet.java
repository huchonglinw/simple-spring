package com.huchonglin.web;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huchonglin.anno.RequestMapping;
import com.huchonglin.util.ClassUtil;
import com.huchonglin.util.WebUtil;

/**
 * http://localhost:8080/simple-spring/login.do http://localhost:8080/simple-spring/ 这一段是Tomcat转发过来的
 * 
 * @author: hcl
 * @date: 2020/7/3 00:55
 */
public class DispatcherServlet extends HttpServlet {
    /**
     * 在 spring-mvc.properties 中配置的名字 spring-mvc.properties packageLocation = "org.com"; Without
     * doubt，这里也可以通过ServletContext拿到web.xml配置的全局参数 或者通过ServletConfig拿到配置的局部Servlet参数。
     */
    private static final String PACKAGE_LOCATION = "packageLocation";

    private static Map<String, Handler> handlerMapping = new HashMap<>();

    private static Properties properties = new Properties();

    /**
     * HandlerMapping的 eg.beanNameUrlHandlerMapping
     */
    // private List<HandlerMapping> handlerMappings = new ArrayList<>();

    // private List<HandlerAdapter> handlerAdapters;

    // private AbstractApplicationContext context;

    /** beanFactory */
    // private AbstractBeanFactory beanFactory;


    private void initServletBean(ServletConfig config) {
        // 返回的是spring-mvc.properties
        String mvcPropertiesName = WebUtil.getMvcPropertiesName(config);

        properties = WebUtil.loadProperties(mvcPropertiesName);
        // 包的路径 类似 com.abc.controller

        String controllerPackageLocation = (String)properties.get(PACKAGE_LOCATION);
        // 扫描类，并加载到子容器 这里先搞一个 HashMap<类名, 类>
        initHandlerMapping(controllerPackageLocation);

        //
        // 还要扫描po类，为参数绑定做准备
        // initWebApplicationContext((String) properties.get(PACKAGE_LOCATION));
        //initStrategies(context);
    }

    private void initHandlerMapping(String controllerPackageLocation) {
        Object controller = null;
        ClassUtil.getPackageClass(controllerPackageLocation);
        Set<Class<?>> classSetOfController = ClassUtil.getClassSetOfController();
        for (Class<?> controllerClass : classSetOfController) {
            try {
                controller = controllerClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String url = "/";
            RequestMapping controllerClassAnnotation = controllerClass.getAnnotation(RequestMapping.class);
            if (controllerClassAnnotation != null) {
                url += controllerClassAnnotation.value();
            }

            // 获取public的方法
            Method[] methods = controllerClass.getMethods();
            for (Method method : methods) {
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                if(annotation!=null){
                    String realUrl = url;
                    realUrl += "/" + annotation.value();
                    Handler handler = new Handler(controllerClass, controller, method, realUrl);
                    handlerMapping.put(realUrl,handler);
                }
            }
        }

    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        //处理req的url
        // URI：/项目名/...
        String targetURL = "";
        String requestURI = req.getRequestURI();
        String[] splits = requestURI.split("/");
        for (int i = 0; i < splits.length; i++) {
            if (i > 0) {
                targetURL += splits[i];
            }
        }

        //找到对应的handler

        //反射调用handler

    }

    /**
     * 初始化方法
     *
     * @param config
     *            暂时使用不到
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) {
        initServletBean(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }

   /* private void initStrategies(AbstractApplicationContext context) {
        // 多文件上传组件...
        // ...
        initHandlerMappings(context);
        initHandlerAdapters(context);
        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);
        initViewResolvers(context);
        // initFlashMapManager(context);
    }*/

}
