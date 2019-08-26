package org.mzj.test.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor{
	private static Map<String, Object> map = new HashMap<String, Object>();

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(beanName.endsWith("Service")) {
			System.out.println("before: " + beanName + " " + bean);
			map.put(beanName, bean);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(beanName.endsWith("Service")) {
			System.out.println("after : " + beanName + " " + bean);
			Object obj = map.get(beanName);
			System.out.println(obj.equals(bean));//false
		}
		return bean;
	}

}
