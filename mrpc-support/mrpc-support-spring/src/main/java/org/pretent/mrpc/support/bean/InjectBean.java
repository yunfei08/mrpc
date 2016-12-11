package org.pretent.mrpc.support.bean;

import org.apache.log4j.Logger;
import org.pretent.mrpc.annotaion.Reference;
import org.pretent.mrpc.client.ProxyFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 处理属性和方法上的@Reference注解,为bean注入代理对象
 */
public class InjectBean {

    private static Logger LOGGER = Logger.getLogger(InjectBean.class);

    public void inject(Object bean) {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            Reference reference = method.getAnnotation(Reference.class);
            if (reference != null) {
                LOGGER.debug("method================" + bean.getClass().getName() + "--->" + method.getName() + "需要生成reference的代理对象");
                System.err.println("method================" + bean.getClass().getName() + "--->" + method.getName() + "需要生成reference的代理对象");
                try {
                    Object value = ProxyFactory.getService(method.getParameterTypes()[0]);
                    if (value != null) {
                        method.invoke(bean, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.getType().isPrimitive()) {
                Reference reference = field.getAnnotation(Reference.class);
                if (reference != null) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(bean);
                        field.setAccessible(false);
                        if (value == null) {
                            LOGGER.debug("field================" + bean.getClass().getName() + "--->" + field.getName() + "需要生成reference的代理对象");
                            System.err.println("field================" + bean.getClass().getName() + "--->" + field.getName() + "需要生成reference的代理对象");
                            value = ProxyFactory.getService(field.getType());
                            if (value != null) {
                                field.setAccessible(true);
                                field.set(bean, value);
                                field.setAccessible(false);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}