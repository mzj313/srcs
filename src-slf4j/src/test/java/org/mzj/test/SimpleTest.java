package org.mzj.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTest {

	@Test
	public void testSlf4j() {
		Logger logger = LoggerFactory.getLogger("slf4j-simple-test");
		System.out.println(logger.isDebugEnabled());
		logger.info("Hello world.");
	}
	
	@Test
	public void testLog4j() {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("org.mzj.test");
		System.out.println(logger.isDebugEnabled());
		System.out.println(logger.getLevel());
		logger.info("Hello world.");
	}
}
