package org.mzj.test;

import org.junit.Test;
import org.mzj.test.bean.ServiceA;
import org.mzj.test.bean.ServiceB;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanLoadTest {
	@Test
	public void testXmlLoading() {
		ApplicationContext ac = new ClassPathXmlApplicationContext("org/mzj/test/bean/beans.xml");
		ServiceA a = ac.getBean(ServiceA.class);
		ServiceB b = ac.getBean(ServiceB.class);
		System.out.println(a);
	}

	@Test
	public void testAnnoLoading() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(ServiceA.class, ServiceB.class);
		ServiceA a = ac.getBean(ServiceA.class);
		ServiceB b = ac.getBean(ServiceB.class);
		System.out.println(a);
	}
}
