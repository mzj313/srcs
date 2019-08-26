package org.mzj.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.mzj.test.service.TaskService;

public class TaskServiceTest extends TestBase {
	@Resource
	TaskService taskService;

	@Test
	public void testTask() {
		taskService.cron = "0/2 * 10-23 * * ?";
		taskService.taskRegistrar.afterPropertiesSet();
		sleep();
	}
}
