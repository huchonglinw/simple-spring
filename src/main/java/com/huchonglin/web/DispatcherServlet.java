package com.huchonglin.web;

import com.huchonglin.anno.RequestMapping;
import com.huchonglin.util.ClassUtil;
import com.huchonglin.util.ServletContextUtil;
import com.huchonglin.util.WebUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * http://localhost:8080/simple-spring/login.do http://localhost:8080/simple-spring/ 这一段是Tomcat转发过来的
 * 
 * @author: hcl
 * @date: 2020/7/3 00:55
 */
public class DispatcherServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
    /**
     * 在 spring-mvc.properties 中配置的名字 spring-mvc.properties packageLocation = "org.com"; Without
     * doubt，这里也可以通过ServletContext拿到web.xml配置的全局参数 或者通过ServletConfig拿到配置的局部Servlet参数。
     */
    private static final String CONTROLLER_PACKAGE_LOCATION = "controllerPackage";

    private static final String PO_PACKAGE_LOCATION = "poPackage";

    private static Map<String, Handler> handlerMapping = new HashMap<>();

    private static Properties properties = new Properties();

    private static Set<Class<?>> classSetOfRepository = new HashSet<>();

    private void initServletBean(ServletConfig config) {

        loadProperties(config);

        loadControllerClass();

        loadPoClass();

        generateMethodParameters();
    }

    private void generateMethodParameters() {

    }

    private void loadPoClass() {
        String poPackage = (String)properties.get(PO_PACKAGE_LOCATION);
        ClassUtil.loadClassFromPackage(poPackage);
        classSetOfRepository = ClassUtil.getClassSetOfRepository();
    }

    /**
     * 加载Controller类
     */
    private void loadControllerClass() {
        // 包的路径 类似 com.abc.controller
        String controllerPackageLocation = (String)properties.get(CONTROLLER_PACKAGE_LOCATION);
        // 扫描类，并加载到子容器 这里先搞一个 HashMap<类名, Handler>
        initHandlerMapping(controllerPackageLocation);
    }

    /**
     * 加载spring-mvc.properties
     * 
     * @param config
     * @return
     */
    private void loadProperties(ServletConfig config) {
        // 返回的是spring-mvc.properties
        String mvcPropertiesName = WebUtil.getMvcPropertiesName(config);
        properties = WebUtil.loadProperties(mvcPropertiesName);
    }

    private void initHandlerMapping(String controllerPackageLocation) {
        Object controller = null;
        ClassUtil.loadClassFromPackage(controllerPackageLocation);
        Set<Class<?>> classSetOfController = ClassUtil.getClassSetOfController();
        for (Class<?> controllerClass : classSetOfController) {
            log.info(String.valueOf(controllerClass));
            try {
                controller = controllerClass.newInstance();
            } catch (Exception e) {
                log.error("实例化失败");
            }
            String url = "";
            RequestMapping controllerClassAnnotation = controllerClass.getAnnotation(RequestMapping.class);
            if (controllerClassAnnotation != null) {
                url += controllerClassAnnotation.value();
            }

            // 获取public的方法
            Method[] methods = controllerClass.getMethods();
            for (Method method : methods) {
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                if (annotation != null) {
                    String realUrl = url;
                    realUrl += annotation.value();
                    Handler handler = new Handler(controllerClass, controller, method, realUrl);
                    handlerMapping.put(realUrl, handler);
                }
            }
        }

    }

    @Test
    public void test() {
        Method[] methods = DispatcherServlet.class.getDeclaredMethods();
        for (Method method : methods) {
            Class<?>[] parameters = method.getParameterTypes();
            for (Class<?> parameter : parameters) {
                System.out.println(parameter.getName());
            }
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        // 保存req，res构造web环境
        ServletContextUtil.getThreadLocal().set(new ServletContextBean(req, resp));

        String uri = WebUtil.resolveRequestUrl(req.getRequestURI());

        Object invoke = invokeMethod(uri);

        // 解析视图
        if (invoke != null) {
            if (invoke instanceof String) {
                try {
                    req.getRequestDispatcher((String)invoke).forward(req, resp);
                } catch (Exception e) {
                    log.error("fail to forward! page :" + invoke);
                }
            }
            if (invoke instanceof ModelAndView) {
                Object object = ((ModelAndView)invoke).getModel();
                String view = ((ModelAndView)invoke).getView();
                req.getSession().setAttribute(((ModelAndView)invoke).getModelName(), ((ModelAndView)invoke).getModel());
                try {
                    req.getRequestDispatcher(view).forward(req, resp);
                } catch (Exception e) {
                    log.error("fail to forward! page :" + invoke);
                }
            }
        }
    }

    private Object invokeMethod(String uri) {
        // 找到对应的handler
        Handler handler = handlerMapping.get(uri);
        if (handler != null) {
            Method method = handler.getMethod();
            Object[] arguments = null;
            // 如果方法有参数，要解析
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes != null) {
                arguments = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    if (parameterType == HttpServletRequest.class) {
                        // 该方法需要拿到req对象，可以弄一个封装类，但为了避免全局
                        HttpServletRequest httpRequest = ServletContextUtil.getHttpRequest();
                        arguments[i] = httpRequest;
                    } else if (parameterType == HttpServletResponse.class) {
                        HttpServletResponse httpResponse = ServletContextUtil.getHttpResponse();
                        arguments[i] = httpResponse;
                    } else {
                        // po类
                        if (!classSetOfRepository.isEmpty()) {
                            if (classSetOfRepository.contains(parameterType)) {
                                Object object = null;
                                try {
                                    object = parameterType.newInstance();
                                    Field[] declaredFields = parameterType.getDeclaredFields();
                                    for (Field declaredField : declaredFields) {
                                        HttpServletRequest httpRequest = ServletContextUtil.getHttpRequest();
                                        String parameter = httpRequest.getParameter(declaredField.getName());
                                        if (parameter != null) {
                                            declaredField.setAccessible(true);
                                            declaredField.set(object, parameter);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                arguments[i] = object;
                            }
                        }
                    }

                }
            }
            handler.setMethodArgments(arguments);
            Object[] methodArguments = handler.getMethodArguments();

            Object invoke = null;
            // 反射调用handler
            try {
                invoke = method.invoke(handler.getControllerObject(), methodArguments);
            } catch (Exception e) {
                log.error("method.invoke() fail");
            }
            return invoke;
        }
        return null;
    }

    /**
     * 初始化方法
     *
     * @param config
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
        // ...
        initHandlerMappings(context);
        initHandlerAdapters(context);
        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);
        initViewResolvers(context);
        // initFlashMapManager(context);
    }*/

}
