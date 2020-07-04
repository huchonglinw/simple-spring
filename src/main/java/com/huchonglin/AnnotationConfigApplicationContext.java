package com.huchonglin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.huchonglin.anno.*;
import com.huchonglin.util.ClassUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 注解的应用上下文
 *
 * @author: hcl
 * @date: 2020/7/2 19:45
 */
@Slf4j
public class AnnotationConfigApplicationContext {
    private static Set<Class<?>> beanClassSet = new HashSet<>();
    private static Set<Class<?>> aspectClassSet = new HashSet<>();
    private static Set<Class<?>> toBeProxyClassSet = new HashSet<>();
    private static Method beforeMethod;
    private static Method afterMethod;
    private Class<?> currentAspectClass;


    /**
     * 狭义上的ioc容器
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /**
     * 缓存容器
     */
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    /**
     * 存放注册信息的BeanDefinition的Map
     */
    protected final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 配置类的构造函数
     */
    public AnnotationConfigApplicationContext(Class<?> configurationClass) {
        register(configurationClass);
        refresh();
    }

    private void register(Class<?> clazz) {
        try {
            Configuration annotation = clazz.getAnnotation(Configuration.class);
            if (annotation != null) {
                String value = annotation.value();
                String[] values = annotation.values();
                if (value != "") {
                    //加载指定路径
                    ClassUtil.getPackageClass(value);
                    beanClassSet = ClassUtil.getClassSetOfBean();
                    aspectClassSet = ClassUtil.getClassSetOfAspect();
                    for (Class<?> aspectClass : aspectClassSet) {
                        Aspect aspect = aspectClass.getAnnotation(Aspect.class);
                        String toBeProxyPackage = aspect.value();
                        ClassUtil.getPackageClass(toBeProxyPackage);
                        toBeProxyClassSet = ClassUtil.getClassSetAll();
                    }
                }
                if (values.length != 0) {
                    // todo
                }
            }
        } catch (Exception e) {
            log.error("类名为：" + clazz + "，实例化对象失败");
        }
    }

    /**
     * 刷新ioc容器
     * 这里的refresh是一个模板方法，后期改进
     */
    public void refresh() {
        //解耦 交给beanFactory去注册bd
        registerBeanDefinition(beanClassSet);
        registerBeanDefinition(aspectClassSet);

        //注册Aspect的类
        for (Class<?> clazz : aspectClassSet) {
            this.currentAspectClass = clazz;
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if(null != declaredMethod.getAnnotation(Before.class)){
                    beforeMethod = declaredMethod;
                }
                if(null != declaredMethod.getAnnotation(After.class)){
                    afterMethod =declaredMethod;
                }
            }
        }


        //proxyInitialization(beanFactory);

        Set<String> keySet = beanDefinitionMap.keySet();
        for (String key : keySet) {
            getBean(key);
        }


        //finishBeanFactoryInitialization(beanFactory);
    }

    public Object getBean(String name) {
        return doGetBean(name);
    }

    /**
     * 真正的getBean业务逻辑
     * 并且要解决循环依赖
     * class User{
     *      @Autowired
     *      Cat cat;
     * }
     *
     * class Cat{
     *      @Autowired
     *      User user;
     * }
     * 逻辑：第一次走到getBean(user)的时候，先
     * @param name
     * @return
     */
    private Object doGetBean(String name) {

        Object singletonObject = this.singletonObjects.get(name);
        if(singletonObject != null){
            return singletonObject;
        }else{
            Object earlySingletonObject = this.earlySingletonObjects.get(name);
            if(earlySingletonObject != null) {
                return earlySingletonObject;
            }else{
                Class<?> clazz = this.beanDefinitionMap.get(name).getClazz();
                Object objectInstance = null;
                try {
                    objectInstance = clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //放入缓存池
                this.earlySingletonObjects.put(name,objectInstance);
                //依赖注入
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if(null != declaredField.getAnnotationsByType(AutoWired.class) ) {
                        String declaredFieldName = declaredField.getType().getSimpleName();

                        Object declaredFieldInstance = doGetBean(declaredFieldName);

                        declaredField.setAccessible(true);
                        try {
                            declaredField.set(objectInstance,declaredFieldInstance);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(toBeProxyClassSet.contains(this.beanDefinitionMap.get(name).getClazz())){
                    //做代理
//                    objectInstance = Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new InvocationHandler() {
//                        @Override
//                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                            Object object = singletonObjects.get(name);
//                            if(object == null) {
//                                return null;
//                            }else{
//                                beforeMethod.invoke(object);
//                                Object invoke = method.invoke(object, args);
//                                afterMethod.invoke(object);
//                                return invoke;
//                            }
//                        }
//                    });

                    try {
                        objectInstance = new JdkProxy(objectInstance,beforeMethod,afterMethod,currentAspectClass.newInstance()).getProxy();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                this.singletonObjects.put(name,objectInstance);
                return objectInstance;
            }
        }
    }

    public void registerBeanDefinition(Set<Class<?>> classSet) {
        for (Class<?> cls : classSet) {
            if (cls.isInterface()) {
                continue;
            }
            BeanDefinition beanDefinition = new BeanDefinition(cls, cls.getSimpleName());
            beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
        }
    }
}
