## 项目目的  
1. 设计模式：设计模式是基于面向对象的三大特性继承多态封装的一些优秀的设计思想（封装模板）。学习原项目的设计思想。  
2. 接口：原项目的接口非常多，提取的非常细。
3. 编码规范
## 使用说明  
#### 一、core模块  
- #### AnnotationConfigApplicationContext("配置类.class")  
>目前只实现了只能传递一个配置类的构造函数  
配置类需要加 **@Configuration(value=" ")** 注解  
- #### @Bean  
>其他的注解后续实现，原理一样  
- #### AOP  
>切面类需要增添 **@Aspect(value="需要被代理的包路径")** 注解  
````java
@Aspect("com.spring.service")
public class demo{
    @After  
    public void afterMethod(){
    }
    @Before  
    public void beforeMethod(){
    }  
}
````  
> 只实现了基于jdk动态代理的，所以必须带接口  
- ####BeanPostProcessor接口
>实现原理：两种(伪代码)  
1.bean.isInterface(beanPostProcessor) ? 反射调用 : continue;  
结果：如果要实现单个Bean的处理逻辑，这是可取的，但是Spring并不是这么用，从源码文档可以看到这段话
>````text
>Apply this BeanPostProcessor to the given new bean instance <i>after</i> any bean
>initialization callbacks
>````  
>也就是说这个beanPostProcessor是应用在所有的bean实例上的
---  
>2.Set<BeanPostProcessor\>，bean.isInterface(beanPostProcessor) ?  
结果：这段代码和实现原理1的思路一样，只不过实现逻辑不同 
  
### 二、web  
- #### web.xml
>配置DispatcherServlet，并且设置容器启动时就加载此Servlet。  
用于拦截指定的所有请求，并在service()方法里做相应处理
- #### 配置文件
```properties
controllerPackage=org.com.controller
poPackage=org.com.po
```

## 个人的
### 代办
1. Util:处理getBean大小写  
2. BeanPostProcessor：设计思想是观察者模式，容器初始化时扫描所有实现
了BeanPostProcessor接口的类并保存在一个数组中，当事情发生时遍历该数组  
3. Mybatis注解原理：动态代理解析sql语句直接执行，因为接口带了注解，被Spring
扫描，代理对象放容器了  
### 问题  
1. 如果将代理对象放入缓存池可能会导致 循环依赖 解决方案失败？  
