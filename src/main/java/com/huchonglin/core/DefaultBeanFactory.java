package com.huchonglin.core;

import com.huchonglin.anno.*;
import com.huchonglin.aop.AopProxy;
import com.huchonglin.aop.JdkProxy;
import com.huchonglin.core.constant.AnnotationTypeConfigConsts;
import com.huchonglin.util.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: hcl
 * @date: 2020/7/5 14:46
 */
public class DefaultBeanFactory implements BeanFactory {
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
    protected static Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    protected static Map<String, ProxyMetaData> proxyMetaDataMap = new HashMap<>();

    private static Set<Class<?>> toBeProxyClassSet = new HashSet<>();

    public void generateBeanDefinition(Set<Class<?>> classSet) {
        for (Class<?> cls : classSet) {
            BeanDefinition beanDefinition = new BeanDefinition(cls, cls.getSimpleName());
            if (cls.getAnnotation(Bean.class) != null) {
                beanDefinition.setAnnotationType(AnnotationTypeConfigConsts.BEAN);
            }
            if (cls.getAnnotation(Aspect.class) != null) {
                beanDefinition.setAnnotationType(AnnotationTypeConfigConsts.ASPECT);
            }
            beanDefinitionMap.put(cls.getSimpleName(), beanDefinition);
        }

    }

    /**
     * 解析AspectClass上的参数
     */
    public void resolveAspectClass() {
        // 先遍历所有的Class元数据
        Set<Map.Entry<String, BeanDefinition>> entrySet = beanDefinitionMap.entrySet();
        for (Map.Entry<String, BeanDefinition> beanDefinition : entrySet) {
            Class<?> clazz = beanDefinition.getValue().getClazz();
            // 挨个判断是否有Aspect
            Aspect aspect = clazz.getAnnotation(Aspect.class);
            if (aspect != null) {
                Method beforeMethod = null;
                Method afterMethod = null;
                Method[] declaredMethods = clazz.getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    if (null != declaredMethod.getAnnotation(Before.class)) {
                        beforeMethod = declaredMethod;
                    }
                    if (null != declaredMethod.getAnnotation(After.class)) {
                        afterMethod = declaredMethod;
                    } else {
                        // todo
                    }
                }
                Object bean = getBean(clazz.getSimpleName());
                ProxyMetaData proxyMetaData = new ProxyMetaData(beforeMethod, afterMethod, bean);

                // 获取到aspect上面值，也就是要被代理的包路径
                String toBeProxyPackage = aspect.value();
                toBeProxyClassSet = ClassUtil.loadClassFromPackage(toBeProxyPackage);
                for (Class<?> toBeProxyClass : toBeProxyClassSet) {
                    BeanDefinition definition = beanDefinitionMap.get(toBeProxyClass.getSimpleName());
                    definition.setToBeProxy(true);
                    proxyMetaDataMap.put(toBeProxyClass.getSimpleName(), proxyMetaData);
                }
            }

        }
    }


    public void refresh() {
        Set<String> keySet = beanDefinitionMap.keySet();
        for (String key : keySet) {
            getBean(key);
        }
    }

    @Override
    public Object getBean(String name) {
        // 单例池
        Object singletonObject = this.singletonObjects.get(name);
        if (singletonObject != null) {
            return singletonObject;
        }

        // 缓存池
        Object earlySingletonObject = this.earlySingletonObjects.get(name);
        if (earlySingletonObject != null) {
            return earlySingletonObject;
        }

        // 正式初始化bean
        Class<?> clazz = beanDefinitionMap.get(name).getClazz();
        Object objectInstance = null;
        try {
            objectInstance = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 放入缓存池
        this.earlySingletonObjects.put(name, objectInstance);
        // 依赖注入
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (null != declaredField.getAnnotationsByType(AutoWired.class)) {
                String declaredFieldName = declaredField.getType().getSimpleName();

                // !
                Object declaredFieldInstance = getBean(declaredFieldName);

                declaredField.setAccessible(true);
                try {
                    declaredField.set(objectInstance, declaredFieldInstance);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        // 代理
        if (ifNecessaryProxy(name)) {
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces != null) {
                try {
                    ProxyMetaData proxyMetaData = proxyMetaDataMap.get(name);
                    JdkProxy jdkProxy =
                        new JdkProxy(objectInstance, proxyMetaData.getBefore(), proxyMetaData.getAfter(), proxyMetaData.getMethodObject());
                    objectInstance = getProxy(jdkProxy);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // todo getProxy(cglib)
            }
        }

        this.singletonObjects.put(name, objectInstance);
        return objectInstance;

    }

    /**
     * 策略模式 具体实现你自己封装，我只负责调用
     *
     * @param proxy
     * @return
     */
    private Object getProxy(AopProxy proxy) {
        return proxy.getProxy();
    }

    private Boolean ifNecessaryProxy(String name) {
        if (toBeProxyClassSet.contains(beanDefinitionMap.get(name).getClazz())) {
            return true;
        }
        return false;
    }

}
