package org.mzj.test;

import org.junit.Test;

import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.topic.DeleteTopicSubCommand;

public class RocketmqAdminTest {

	@Test
	public void clearMsg() {
		try {
			DefaultMQAdminExt adminExt = new DefaultMQAdminExt();
			adminExt.setNamesrvAddr("127.0.0.1:9876");
			adminExt.start();
			String clusterName = "DefaultCluster";
			String topic = "Topic1";
			DeleteTopicSubCommand.deleteTopic(adminExt,clusterName,topic);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
