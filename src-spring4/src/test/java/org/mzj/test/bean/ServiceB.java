package org.mzj.test.bean;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class ServiceB implements InitializingBean {
	public ServiceB(){
		System.out.println("ServiceB.ServiceB()");
	}

	@PostConstruct
	public void postContruct() {
		System.out.println("ServiceB.postContruct()");
	}
	
	public void afterPropertiesSet() throws Exception {
		System.out.println("ServiceB.afterPropertiesSet()");
	}

	public void initMethod() {
		System.out.println("ServiceB.initMethod()");
	}
	
	public static void staticMethod() {
		
	}
}
