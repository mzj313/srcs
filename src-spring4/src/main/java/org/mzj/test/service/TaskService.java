package org.mzj.test.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class TaskService implements SchedulingConfigurer {
	public String cron = "0 0/2 12-23 * * ?";
	public ScheduledTaskRegistrar taskRegistrar;
	
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		System.out.println("====TaskService.configureTasks| configureTasks()...");
		this.taskRegistrar = taskRegistrar;
		
		Runnable task = new Runnable() {
			public void run() {
				SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				System.out.println("====TaskService| task.run() now=" + dateFormat.format(new Date()));
			}
		};
		Trigger trigger = new Trigger() {
			public Date nextExecutionTime(TriggerContext triggerContext) {
				System.out.println("====TaskService| Trigger.nextExecutionTime() cron=" + cron);
				CronTrigger trigger = new CronTrigger(cron);
				Date nextExecDate = trigger.nextExecutionTime(triggerContext);
				return nextExecDate;
			}
		};
		TriggerTask triggerTask = new TriggerTask(task, trigger);
//		taskRegistrar.setTriggerTasksList(Arrays.asList(triggerTask));
		taskRegistrar.addTriggerTask(task, trigger);
	}
}
