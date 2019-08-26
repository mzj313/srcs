package org.mzj.test.bean;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceA implements InitializingBean {
	@Autowired
	ServiceB serviceB;

	public ServiceA() {
		System.out.println("ServiceA.ServiceA() " + serviceB);
	}

	@PostConstruct
	public void postContruct() {
		System.out.println("ServiceA.postContruct() " + serviceB);
	}

	public void afterPropertiesSet() throws Exception {
		System.out.println("ServiceA.afterPropertiesSet() " + serviceB);
	}

	public void initMethod() {
		System.out.println("ServiceA.initMethod() " + serviceB);
	}
}
