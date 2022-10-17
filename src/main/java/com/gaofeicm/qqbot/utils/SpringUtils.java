package com.gaofeicm.qqbot.utils;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Gaofeicm
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = applicationContext == null ? context : applicationContext;
    }

    /**
     * 获取applicationContext
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     * @param name beanName
     * @return bean对象
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     * @param clazz class
     * @param <T> T
     * @return T
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     * @param name beanName
     * @param clazz class
     * @param <T> T
     * @return T
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 反射调用SpringBean容器内对象方法
     * @param beanObject Bean对象
     * @param methodName 方法名
     * @param parameterTypes 参数类型
     * @param obj 参数
     * @return Object结果
     * @throws Exception
     */
    public static Object invokeMethod(Object beanObject, String methodName, Class<?> [] parameterTypes, JSONObject obj) throws Exception{
        return beanObject == null ? new Object() : beanObject.getClass().getMethod(methodName, parameterTypes).invoke(beanObject, obj);
    }
}
