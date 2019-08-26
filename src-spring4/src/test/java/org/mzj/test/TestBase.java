package org.mzj.test;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-service.xml"})
public class TestBase {
	@Autowired
	protected ApplicationContext ctx;
	
	@Before
	public void setUp() {
		System.out.println("--------------setUp--------------");
	}
	
	@After
	public void tearDown() {
		System.out.println("--------------tearDown--------------");
	}
	
	@Test
	public void test() {
	}
	
	public void sleep() {
		try {
			TimeUnit.MINUTES.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
